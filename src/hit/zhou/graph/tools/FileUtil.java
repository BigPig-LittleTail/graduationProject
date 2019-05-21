package hit.zhou.graph.tools;


import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;

public class FileUtil {
	private enum FileType{
		TXT_UTF8,DOC,DOCX,PURE
	}


	private static byte[] read(String filePath) throws IOException {
		InputStream in = new FileInputStream(filePath);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int n;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		byte[] data = out.toByteArray();
		in.close();
		return data;
	}

	public static String readString(String filePath) throws IOException{
		FileType fileType = checkType(filePath);
		System.err.println(filePath);
		switch (fileType){
			case PURE:
				return new String(read(filePath));
			case DOC:
				return contextOfDoc(filePath);
			case DOCX:
				return contextOfDocx(filePath);
			case TXT_UTF8:
				byte[] data = read(filePath);
				return new String(data,3,data.length - 3);
			default:
				return "";
		}
	}

	private static String contextOfDocx(String filePath){
		String str = "";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			XWPFDocument xdoc = new XWPFDocument(fis);
			XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
			str = extractor.getText();
			extractor.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	private static String contextOfDoc(String filePath){
		String str = "";
		try {
			FileInputStream in = new FileInputStream(filePath);
			HWPFDocument doc = new HWPFDocument(in);
			str = doc.getDocumentText();
			doc.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}



	/**
	 * 用来检查读入文件类型
	 * @param filePath 文件路径
	 * @return 文件类型
	 * @throws IOException 异常
	 */
	private static FileType checkType(String filePath) throws IOException{
		InputStream in = new FileInputStream(filePath);
		byte[] fileType = new byte[4];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int n;
		while ((n = in.read(fileType)) != -1 && n < fileType.length) {
			out.write(fileType, 0, n);
		}
		int type = 0;

		for(int i = 0;i<=3;i++){
			type = (type << 8)|((fileType[i] & 0xff));
		}

		if(type == 0x504b0304){
			return FileType.DOCX;
		}
		else if(type == 0xd0cf11e0){
			return FileType.DOC;
		}
		else if((type & 0xffffff00) == 0xefbbbf00){
			return FileType.TXT_UTF8;
		}
		else {
			return FileType.PURE;
		}
	}


	public static void save(String savePath, byte[] content, boolean isAppend){
		try {
			File file = new File(savePath);
			OutputStream os = new FileOutputStream(file,isAppend);
			os.write(content, 0, content.length);
			os.flush();
			os.close();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}

}
