package hit.zhou.hepler;


import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;

/**
 * 文件操作工具类
 */
public class FileUtil {
	/**
	 * 读取文件内容为二进制数组
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(String filePath) throws IOException {
		InputStream in = new FileInputStream(filePath);
		byte[] data = inputStream2ByteArray(in);
		in.close();
		return data;
	}

	public static String readString(String filePath) throws IOException{
		File file = new File(filePath);
		//如果是txt文件，直接读入
		if(file.getName().matches(".*\\.txt$")
		 && file.isFile()){
			System.err.println(file.getName());
			return new String(read(filePath));
		}
		String fileTypeDocOrDocx = checkDocOrDocx(filePath);

		if(fileTypeDocOrDocx.equals("doc")){
			return contextOfDoc(filePath);
		}
		else if(fileTypeDocOrDocx.equals("docx")){
			return contextOfDocx(filePath);
		}
		else {
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
	 * 用来检查读入文件类型，docx，doc或者other
	 * @param filePath 文件路径
	 * @return 文件类型
	 * @throws IOException 异常
	 */
	private static String checkDocOrDocx(String filePath) throws IOException{
		InputStream in = new FileInputStream(filePath);
		byte[] fileType = new byte[4];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int n = 0;
		while ((n = in.read(fileType)) != -1 && n < fileType.length) {
			out.write(fileType, 0, n);
		}
		int type = 0;
		for(int i = 3;i>=0;i--){
			type = type | (fileType[i] << (3-i) * 8);
		}

		if(type == 0x504b0304){
			return "docx";
		}
		else if(type == 0xd0cf11e0){
			return "doc";
		}
		else {
			return "other";
		}
	}


	/**
	 * 流转二进制数组
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static byte[] inputStream2ByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

	/**
	 * 保存文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @param content
	 */
	public static void save(String filePath, String fileName, byte[] content,boolean isAppend) {
		try {
			File filedir = new File(filePath);
			if (!filedir.exists()) {
				filedir.mkdirs();
			}
			File file = new File(filedir, fileName);
			OutputStream os = new FileOutputStream(file,isAppend);
			os.write(content, 0, content.length);
			os.flush();
			os.close();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}

}
