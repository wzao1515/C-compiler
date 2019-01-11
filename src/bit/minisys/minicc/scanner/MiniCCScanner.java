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
		/* ��Źؼ��ֺͱ�ʶ�� */
		byte[] word = new byte[LENGTHOFKEYWORDS];
		// System.out.println(line);
		/* ������� */
		byte[] number = new byte[LENGTHOFINT];
		/* �������� */
		byte[] symbol = new byte[500];
		/* ����ַ������������ַ����� */
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

			// ����DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();

			// ����Document
			Document document = builder.newDocument();

			// ����XML������standaloneΪyes����û��dtd��schema��Ϊ��XML��˵���ĵ����Ҳ���ʾ������
			// document.setXmlStandalone(true);

			// �������ڵ�
			Element project = document.createElement("project");
			// �����ӽڵ㣬����������
			Element tokens = document.createElement("tokens");
			// tokens.setAttribute("id", "1");
			int c = 0;
			for (int i = 0; i < count; i++) {
				// Ϊtokens����ӽڵ�
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
			// Ϊ���ڵ�����ӽڵ�
			project.appendChild(tokens);

			// �����ڵ���ӵ�Document��
			document.appendChild(project);

			/*
			 * ���濪ʼʵ�֣� ����XML�ļ�
			 */

			// ����TransformerFactory����
			TransformerFactory tff = TransformerFactory.newInstance();

			// ����Transformer����
			Transformer tf = tff.newTransformer();

			// �����������ʱ����
			tf.setOutputProperty(OutputKeys.INDENT, "yes");

			// ʹ��Transformer��transform()������DOM��ת����XML
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
		// �洢�����ָ��
		int tempPosition = -1;
		do {
			tempPosition++;
			symbol[tempPosition] = temp[position];
			// �ַ����Ƚ���1λ
			position++;
			if (position >= temp.length) /* ����Ѿ��������޾���ֹѭ���� */ {
				String checkdWord = new String(symbol, 0, tempPosition + 1);
				position = decideSymble(checkdWord, temp, position);
				return position;
			}
		} while (Symbol.isSingleSymble(new String(temp, position, 1)));
		String checkdWord = new String(symbol, 0, tempPosition + 1);
		// ���������������ɷ��������
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
			// �洢�����ָ��
			int tempPosition = -1;
			do {
				tempPosition++;
				string[tempPosition] = temp[position];
				position++;
			} while (temp[position] != '"' && temp[position - 1] != '\\');
			// Ҫ�����һ��"�����˵�������Ҫ�����һ��λ�á�
			tempPosition++;
			string[tempPosition] = '"';
			String checkdNumber = new String(string, 0, tempPosition + 1);
			this.temp.add(checkdNumber);
			this.type.add("identifier");
		}
		// �����ַ�����
		else if (temp[position] == '\'') {
			// �洢�����ָ��
			int begin = 0;

			while (temp[position] != '\'') {
				string[begin++] = temp[position++];
			}
			begin++;
			string[begin] = '\'';
			// Ҫ�����һ��"�����˵�������Ҫ�����һ��λ�á�
			String checkdNumber = new String(string, 0, begin + 1);
			// c�����﷨�涨�ַ��������ַ���ֻ����1���ټ���2�������պ�Ӧ����3��
			if (begin + 1 <= 3) {
				this.temp.add(checkdNumber);
				this.type.add("identifier");
			} else {
				this.temp.add(checkdNumber);
				this.type.add("separator");
			}

		}
		// �����޽��
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
			if (p >= temp.length) /* ����Ѿ��������޾���ֹѭ���� */ {
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
			/* ����Ѿ��������޾���ֹѭ���� */
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
			// System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
			reader = new BufferedReader(new FileReader(file));

			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
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
