package syau.edu.cn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConstPath {
	private static String base_path;
	public ConstPath(String rootpath) {
		base_path = rootpath;
	}
	public static String getBase_Path() {
		//获得当前的项目路径
//		String path = new File("").getAbsolutePath();
//		System.out.println("基本路径"+path);
//		File file = new File(path+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+File.separator+"download");
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		//获取webapp下的路径
//		return path+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+File.separator+"download";
		String path = base_path+File.separator+"download";
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		System.out.println(base_path+"download");
		return base_path+"download";
	}
	public static void main(String[] args) throws URISyntaxException, IOException {
//		String path = new File("").getAbsolutePath();
//		System.out.println(path);
//		String path2 = path+File.separator+"src"+File.separator+"main"+File.separator+"webapp";
//		File file = new File(path2);
//		String result = "";
//		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//		while((result=reader.readLine())!=null) {
//			System.out.println(result);
//		}
//		System.err.println(file);
		System.out.println("s");
		System.out.println(ConstPath.class.getClassLoader().getResource("1.txt"));
		System.out.println(InetAddress.getLocalHost());
	}

}
