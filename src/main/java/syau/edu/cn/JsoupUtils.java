package syau.edu.cn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.border.TitledBorder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import syau.edu.cn.pojo.MyFile;

public class JsoupUtils {
	public static void main(String[] args) throws IOException {
		connection();
	}
	public static List<MyFile> connection() throws IOException {
		List<MyFile> list = new ArrayList<MyFile>();
//		while(matcher.find()) {
//			String url = "http://jwc.syau.edu.cn"+matcher.group(1);
//			String fileDir = matcher.group(2);
//			System.out.println(url+'\t'+fileDir);
//			DownloadUtils.downloadFile(fileDir, url);
//		}
		//先定一次个文件的完成
		String BASE_PATH = ConstPath.getBase_Path();
		if(new File(BASE_PATH).listFiles().length>0){
			System.out.println("不需重新爬去");
			//不需要重新爬取
			list = DownloadUtils.getHerfs(BASE_PATH);
		}else {
			String string = "http://jwc.syau.edu.cn/JiaoXueJB/default.mspx?page=1";
			Document document= Jsoup.connect(string).get();
			String reg = "<li><a href='(.*?)' title='(.*?)'";//匹配连接
			String regex = "<li><a href='(.*?)' title='第(.*?)期《教学信息》简报'";
			//Pattern compile = Pattern.compile(reg);
			Pattern compile = Pattern.compile(regex);
			String string2 = document.html();
			String string3 = string2.replaceAll("\"", "'");
			Matcher matcher = compile.matcher(string3);
			
			while(matcher.find()) {
				String url = "http://jwc.syau.edu.cn"+matcher.group(1);
				String title = matcher.group(2);
				String fileDir = BASE_PATH+File.separator+title;
				System.out.println(url+'\t'+title);
				//这里将标题处理为了所存的文件夹,并将压缩文件进行解压
				MyFile myFile = DownloadUtils.downloadFile(fileDir, url);
				System.out.println(myFile);
				list.add(myFile);
			}
			System.out.println("爬取结束");
			System.out.println(list);
		}
		if(list.size()>0) {
			System.out.println("successful");
			return list;
		}else {
			return null;
		}
		
	}
	
	
}
