package bit.minisys.minicc;

public class MiniCCCfg {
	// input and output for MiniCCPreProcessor
	public static String MINICC_PP_INPUT_EXT = ".c";//CԴ����
	public static String MINICC_PP_OUTPUT_EXT = ".pp.c";//ɾ������ע�ͺͿո񣬺��滻���ļ�����
	// input and output for MiniCCScanner
	public static String MINICC_SCANNER_INPUT_EXT = ".pp.c";//Ԥ�������C����
	public static String MINICC_SCANNER_OUTPUT_EXT = ".token.xml";//�ʷ����������������ַ���
	// input and output for MiniCCParser
	public static String MINICC_PARSER_INPUT_EXT = ".token.xml";//�ʷ�������������ַ���
	public static String MINICC_PARSER_OUTPUT_EXT = ".tree.xml";//�﷨�����������﷨��
	// input and output for MiniCCSemantic
	public static String MINICC_SEMANTIC_INPUT_EXT = ".tree.xml";//�﷨��
	public static String MINICC_SEMANTIC_OUTPUT_EXT = ".tree2.xml";//������
	// input and output for MiniCCICGen
	public static String MINICC_ICGEN_INPUT_EXT = ".tree2.xml";//�﷨��
	public static String MINICC_ICGEN_OUTPUT_EXT = ".ic.xml";//������Ԫʽ�б�
	// input and output for MiniCCOpt
	public static String MINICC_OPT_INPUT_EXT = ".ic.xml";//�м����
	public static String MINICC_OPT_OUTPUT_EXT = ".ic2.xml";//ʵʩ�����ϲ��ȴ����Ż�
	// input and output for MiniCCCodeGen
	public static String MINICC_CODEGEN_INPUT_EXT = ".ic2.xml";//�м����
	public static String MINICC_CODEGEN_OUTPUT_EXT = ".code.asm";//����x86����MIPS������
	// input and output for simulator
	public static String MINICC_ASSEMBLER_INPUT_EXT = ".code.asm";//Ŀ�����
	
	//structure for config.xml
	public String type;
	public String path;
	public String skip;
}
