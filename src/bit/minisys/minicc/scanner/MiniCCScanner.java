package bit.minisys.minicc.scanner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import library.*;

public class MiniCCScanner implements IMiniCCScanner {
	private List<String> temp = new ArrayList<String>();
	private List<String> type = new ArrayList<String>();

	public List<String> getTemp() {
		return temp;
	}

	private static final int LENGTHOFKEYWORDS = 500;

	private static final int LENGTHOFINT = 500;

	private static final int LENGTHOFSTRING = 1024;
	String result = "";

	public void process(String line, String outfile) {
		/* 存放关键字和标识符 */
		byte[] word = new byte[LENGTHOFKEYWORDS];
		// System.out.println(line);
		/* 存放数字 */
		byte[] number = new byte[LENGTHOFINT];
		/* 存放运算符 */
		byte[] symbol = new byte[500];
		/* 存放字符串常数或是字符常数 */
		byte[] string = new byte[LENGTHOFSTRING];
		byte temp[];
		int count = 0;
		byte t;
		temp = line.getBytes();
		for (int position = 0; position < line.length(); position++) {
			if (position >= temp.length) {
				return;
			}
			t = temp[position];
			if (t != ' ' || t != '\t') {
				if ((t >= 'a' && t <= 'z') || (t >= 'A' && t <= 'Z') || t == '_') {
					position = word(position, word, temp);
					count++;
				}
				if ((t >= '0' && t <= '9')) {
					position = number(position, number, temp);
					count++;
				}
				if (Symbol.isLimitedSymblel(new String(temp, position, 1))) {
					position = LimitSymble(position, string, temp);
					count++;
				}
				if (Symbol.isSingleSymble(new String(temp, position, 1))) {
					position = SingleSymble(position, symbol, temp);
					count++;
				}

			}
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {

			// 创建DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();

			// 创建Document
			Document document = builder.newDocument();

			// 设置XML声明中standalone为yes，即没有dtd和schema作为该XML的说明文档，且不显示该属性
			// document.setXmlStandalone(true);

			// 创建根节点
			Element project = document.createElement("project");
			// 创建子节点，并设置属性
			Element tokens = document.createElement("tokens");
			// tokens.setAttribute("id", "1");
			int c = 0;
			for (int i = 0; i < count; i++) {
				// 为tokens添加子节点
				c++;
				Element token = document.createElement("token");
				tokens.appendChild(token);
				Element Number = document.createElement("number");
				Number.setTextContent(String.valueOf(c));
				token.appendChild(Number);
				Element value = document.createElement("value");
				value.setTextContent(this.temp.get(i));
				token.appendChild(value);
				Element Type = document.createElement("type");
				Type.setTextContent(this.type.get(i));
				token.appendChild(Type);
				Element Line = document.createElement("line");
				Line.setTextContent("1");
				token.appendChild(Line);
				Element valid = document.createElement("valid");
				valid.setTextContent("true");
				token.appendChild(valid);
				System.out.println(this.temp.get(i) + " " + this.type.get(i));
			}
			c++;
			Element token = document.createElement("token");
			tokens.appendChild(token);
			Element Number = document.createElement("number");
			Number.setTextContent(String.valueOf(c));
			token.appendChild(Number);
			Element value = document.createElement("value");
			value.setTextContent("#");
			token.appendChild(value);
			Element Type = document.createElement("type");
			Type.setTextContent("#");
			token.appendChild(Type);
			Element Line = document.createElement("line");
			Line.setTextContent("1");
			token.appendChild(Line);
			Element valid = document.createElement("valid");
			valid.setTextContent("true");
			token.appendChild(valid);
			// 为根节点添加子节点
			project.appendChild(tokens);

			// 将根节点添加到Document下
			document.appendChild(project);

			/*
			 * 下面开始实现： 生成XML文件
			 */

			// 创建TransformerFactory对象
			TransformerFactory tff = TransformerFactory.newInstance();

			// 创建Transformer对象
			Transformer tf = tff.newTransformer();

			// 设置输出数据时换行
			tf.setOutputProperty(OutputKeys.INDENT, "yes");

			// 使用Transformer的transform()方法将DOM树转换成XML
			tf.transform(new DOMSource(document), new StreamResult(outfile));

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int SingleSymble(int position, byte[] symbol, byte temp[]) {
		// 存储数组的指针
		int tempPosition = -1;
		do {
			tempPosition++;
			symbol[tempPosition] = temp[position];
			// 字符长度仅限1位
			position++;
			if (position >= temp.length) /* 如果已经超过界限就终止循环。 */ {
				String checkdWord = new String(symbol, 0, tempPosition + 1);
				position = decideSymble(checkdWord, temp, position);
				return position;
			}
		} while (Symbol.isSingleSymble(new String(temp, position, 1)));
		String checkdWord = new String(symbol, 0, tempPosition + 1);
		// 单个运算符可能组成符合运算符
		position = decideSymble(checkdWord, temp, position);
		position--;
		return position;
	}

	private int decideSymble(String checkdWord, byte temp[], int position) {
		if (Symbol.isComboSymbol(checkdWord)) {
			this.temp.add(checkdWord);
			this.type.add("double operator");
		} else if (Symbol.isSingleSymble(checkdWord)) {
			this.temp.add(checkdWord);
			this.type.add("operator");
		}

		return position;
	}

	public int LimitSymble(int position, byte[] string, byte[] temp) {
		if (temp[position] == '"') {
			// 存储数组的指针
			int tempPosition = -1;
			do {
				tempPosition++;
				string[tempPosition] = temp[position];
				position++;
			} while (temp[position] != '"' && temp[position - 1] != '\\');
			// 要把最后一个"给过滤掉，所以要向后移一个位置。
			tempPosition++;
			string[tempPosition] = '"';
			String checkdNumber = new String(string, 0, tempPosition + 1);
			this.temp.add(checkdNumber);
			this.type.add("identifier");
		}
		// 过滤字符常数
		else if (temp[position] == '\'') {
			// 存储数组的指针
			int begin = 0;

			while (temp[position] != '\'') {
				string[begin++] = temp[position++];
			}
			begin++;
			string[begin] = '\'';
			// 要把最后一个"给过滤掉，所以要向后移一个位置。
			String checkdNumber = new String(string, 0, begin + 1);
			// c语言语法规定字符常量的字符数只能是1，再加上2个’，刚好应该是3。
			if (begin + 1 <= 3) {
				this.temp.add(checkdNumber);
				this.type.add("identifier");
			} else {
				this.temp.add(checkdNumber);
				this.type.add("separator");
			}

		}
		// 过滤限界符
		else {
			this.temp.add(new String(temp, position, 1));
			this.type.add("separator");
		}
		return position;
	}

	public int number(int p, byte[] number, byte[] temp) {
		int begin = 0;
		while ((temp[p] >= '0' && temp[p] <= '9') || temp[p] == '.' || (temp[p] >= 'a' && temp[p] <= 'z')
				|| (temp[p] >= 'A' && temp[p] <= 'Z')) {
			number[begin++] = temp[p++];
			if (p >= temp.length) /* 如果已经超过界限就终止循环。 */ {
				String checkdNumber = new String(number, 0, begin + 1);
				decideNum(checkdNumber);
				return p;
			}
		}
		String checkdNumber = new String(number, 0, begin);
		decideNum(checkdNumber);
		p--;
		return p;
	}

	private void decideNum(String checkdNumber) {
		if (Digit.hasDot(checkdNumber)) {
			try {
				this.temp.add(checkdNumber);
				this.type.add("float constant");
			} catch (Exception e) {
				this.temp.add(checkdNumber);
				this.type.add("float constant");
			}
		} else {
			try {
				this.temp.add(checkdNumber);
				this.type.add("integer constant");
			} catch (Exception e) {
				this.temp.add(checkdNumber);
				this.type.add("integer constant");
			}
		}
	}

	public int word(int p, byte[] word, byte[] temp) {
		int begin = 0;
		while ((temp[p] >= 'a' && temp[p] <= 'z') || (temp[p] >= 'A' && temp[p] <= 'Z') || temp[p] == '_'
				|| (temp[p] >= '0' && temp[p] <= '9')) {
			word[begin++] = temp[p++];
			/* 如果已经超过界限就终止循环。 */
			if (p >= temp.length) {
				String checkdWord = new String(word, 0, begin + 1);
				decideWord(checkdWord);
				return p;
			}
		}
		String checkdWord = new String(word, 0, begin);
		decideWord(checkdWord);
		p--;
		return p;
	}

	private void decideWord(String checkdWord) {
		if (KeyWords.isKeyWords(checkdWord)) {
			this.temp.add(checkdWord);
			this.type.add("keywords");
		}

		else {
			this.temp.add(checkdWord);
			this.type.add("identifier");
		}
	}

	public String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		String tempString = null;
		try {
			// System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));

			// 一次读入一行，直到读入null为文件结束
			tempString = reader.readLine();
			// System.out.println(tempString);
			reader.close();
			// reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return tempString;
	}

	public void run(String ppOutFile, String scOutFile) {
		// TODO Auto-generated method stub
		String line = readFileByLines(ppOutFile);
		process(line, scOutFile);
	}
}
