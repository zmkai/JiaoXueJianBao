package syau.edu.cn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.set.SynchronizedSet;
import org.apache.commons.vfs2.FileType;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Expand;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import syau.edu.cn.pojo.MyFile;

public class DownloadUtils {
	//下载文件的压缩包，然后返回一个经后台封装好的实体对象
	public static MyFile downloadFile(String fileDir,String url) throws IOException {
		File dir = new File(fileDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}    
		
		Connection connect = Jsoup.connect(url);
		
		String html = connect.get().html().replaceAll("\"", "");
		//正则匹配文件链接
		String regex = "<a href=(.*?)>文件下载";
		Pattern compile = Pattern.compile(regex);
		Matcher matcher = compile.matcher(html);
		matcher.find();
		//匹配到文件的下载连接
		String fileUrl = matcher.group(1);
		//String fileType = fileUrl.substring(fileUrl.lastIndexOf(".")+1);
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		//拼接出文件的存储路径
		File zip = new File(fileDir+File.separator+fileName);
		FileOutputStream out = new FileOutputStream(zip);
		int len = 0;
		byte[] buffer = new byte[1024];
		InputStream inputStream = new URL(fileUrl).openStream();
		while((len = inputStream.read(buffer))!=-1) {
			out.write(buffer, 0, len);
		}
		out.close();
		inputStream.close();
		//解压文件
		jieya(zip);
		//System.out.println("successful");
		//为某个文件夹下的中文名称的文件重命名
		return FileReName(new File(zip.getParent()));
	}
	
	
	public static void jieya(File file) {
		//解压文件
		MyHeadFile(file);
	}

	//递归处理当文件是文件夹时的情况
	public static void MyHeadFile(File file) {
		//System.out.println("处理文件");
		if(file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				MyHeadFile(file2);
			}
		}else {
			String fileType = file.getPath().substring(file.getPath().lastIndexOf(".")+1).toLowerCase();
			if("zip".equals(fileType)) {
				//System.out.println("zip解压");
				unZip(file.getPath(), file.getParent());
			}else if("rar".equals(fileType)) {
				//System.out.println("rar解压");
				unRAR(file.getPath(),file.getParent());
			}
		}
	}
	
	//使用ant解压zip
	public static void unZip(String sourceZip,String destDir) {
		Project project = new Project();
		Expand expand = new Expand();
		expand.setProject(project);
		expand.setSrc(new File(sourceZip));
		expand.setOverwrite(false);
		expand.setDest(new File(destDir));
		expand.setEncoding("gbk");
		expand.execute();
		
	}
	 /**  
	    * 解压rar格式压缩包。  
	    * 对应的是java-unrar-0.3.jar，但是java-unrar-0.3.jar又会用到commons-logging-1.1.1.jar  
	 * @throws Exception 
	    */   
	public static void unRAR(String sourceRar,String destDir){
		Archive a = null;    
		FileOutputStream fos = null;    
		try{    
			a = new Archive(new File(sourceRar)); 
			
			FileHeader fh = a.nextFileHeader(); 
			while(fh!=null){    
				//防止文件名中文乱码问题的处理
				String compressFileName = fh.getFileNameW().isEmpty()?fh.getFileNameString():fh.getFileNameW();    
				if(!fh.isDirectory()){    
					//1 根据不同的操作系统拿到相应的 destDirName 和 destFileName    
					String destFileName = "";    
					String destDirName = "";    
					//非windows系统    
					if(File.separator.equals("/")){    
						destFileName = destDir +File.separator+ compressFileName.replaceAll("\\\\", "/");    
						destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));    
						//windows系统     
					}else{   
						destFileName = destDir+File.separator + compressFileName.replaceAll("/", "\\\\");    
						destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));    
					}    
					//2创建文件夹    
					File dir = new File(destDirName);  
					if(!dir.exists()||!dir.isDirectory()){    
						dir.mkdirs();    
					}    
					//3解压缩文件    
					fos = new FileOutputStream(new File(destFileName));    
					a.extractFile(fh, fos);    
					fos.close();    
					fos = null;    
				}    
				fh = a.nextFileHeader();    
			}    
			a.close();    
			a = null;    
		}catch(Exception e){
			e.printStackTrace();
		}finally{    
			if(fos!=null){    
				try{
					fos.close();fos=null;
				}catch(Exception e){
					e.printStackTrace();
				}    
			}    
			if(a!=null){    
				try{
					a.close();a=null;
				}catch(Exception e){
					e.printStackTrace();
				}    
			}    
		}  
	}
	
	//文件重命名,并且返回一个封装好的实体对象,如果不存在则返回空
	public static MyFile FileReName(File file) {
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			if(file2.isDirectory()) {
				FileReName(file2);
			}else {
				String fileType = file2.getPath().substring(file2.getPath().lastIndexOf(".")+1);
				if(!"rar".equals(fileType)&&!"zip".equals(fileType)) {
					Copy copy = new Copy();
					Project project = new Project();
					copy.setProject(project);
					copy.setFile(file2);
					String newFileName = "";
					String regex = "([\\d|-]{3,})";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(file2.getName());
					while(matcher.find()) {
						newFileName = matcher.group();
					}
					String newFilePath = file2.getPath().replaceAll(file2.getName(), newFileName+"."+fileType);
					copy.setTofile(new File(newFilePath));
					copy.execute();
					//System.out.println("重命名成功");
					//封装出一个文件对象，对象中包含文件名称，链接，以及真实存储位置
					return FengZhuangLink(newFilePath);
				}
			}
		}
		return null;
	}
	//拼接出一个文件实体，便于返回
	public static MyFile FengZhuangLink(String filePath) {
		MyFile myFile = new MyFile();
		//myFile.setRealPath(filePath);
		//System.out.println(filePath);
		//D:\\eclipse_wordspace2\\jiaoXueJianBao\\src\\main\\webapp\\download\\305\\2017-13.pdf
		String fileName = filePath.substring(filePath.lastIndexOf("download")+9, filePath.lastIndexOf("\\"));
		//System.out.println(fileName);
		//myFile.setTitle("第"+fileName+"期教学简报");
		myFile.setTitle(fileName);
		String linkhou = filePath.substring(filePath.lastIndexOf("download")-1).replace("\\", "/");
		//System.out.println(linkhou);
		try {
			String linkqian = InetAddress.getLocalHost().getHostAddress();
			linkqian="http://"+linkqian+":8080"+"/jiaoXueJianBao";
			String link = linkqian+linkhou;
			//System.out.println(link);
			myFile.setHref(link);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return myFile;
	}
	//当download中存在文件的时候则直接拼接出连接，不需要重新爬取
	public static void getFileList(String path,List<MyFile> lists){
		File dir = new File(path);
		File[] listFiles = dir.listFiles();
		for (File file : listFiles) {
			if(file.isDirectory()) {
				//System.out.println(file.getPath()+"是文件夹");
				getFileList(file.getPath(),lists);
			}else {
				//System.out.println("处理文件");
				String filepath = file.getPath();
				String fileType = filepath.substring(filepath.lastIndexOf(".")+1);
				if("pdf".equals(fileType)||"doc".equals(fileType)) {
					String regex = "^([\\d]|-)+.[a-z]{3}";
					Pattern compile = Pattern.compile(regex);
					Matcher matcher = compile.matcher(file.getName());
					if(matcher.find()) {
						//System.out.println(file.getPath());
						lists.add(FengZhuangLink(filepath));
					}
				}
			}
		}
		
	}
	
	public static List<MyFile> getHerfs(String path){
		List<MyFile> lists = new ArrayList<MyFile>();
		getFileList(path, lists);
		return lists;
	}
	public static void main(String[] args) throws Exception {
//		String url = "http://jwc.syau.edu.cn/JiaoXueJB/32578B25E08DAA6F.shtml";
//		String fileDir ="310";
//		downloadFile(fileDir,url);
		//String BASE_PATH = ConstPath.getBase_Path();
		//BASE_PATH += File.separator+"322";
//		File file = new File(BASE_PATH);
//		File[] listFiles = file.listFiles();
//		for (File file2 : listFiles) {
//			System.out.println(file2.getPath());
			//unZip(file2.getPath(), BASE_PATH);
			//System.out.println(file2.getParent());
//			//unRAR(file2.getPath(), BASE_PATH);
//			MyHeadFile(file2);
//		}
//		System.out.println("successful");
//		System.out.println("开始匹配");
//		String regex = "([\\d|-]{3,})";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher("第308期《教学信息》简报");
//		while(matcher.find()) {
//			System.out.println(matcher.group());
//		}
//		String BASE_PATH = ConstPath.getBase_Path();
//		BASE_PATH += File.separator+"305";	
//		FileReName(new File(BASE_PATH));
//		System.out.println("successful");
	//	FengZhuangLink("D:\\eclipse_wordspace2\\jiaoXueJianBao\\src\\main\\webapp\\download\\305\\2017-13.pdf");
	//	getFileList(ConstPath.getBase_Path());
//		System.out.println("start");
//		//List<MyFile> herfs = getHerfs(ConstPath.getBase_Path());
//		List<MyFile> herfs=getHerfs("D:\\eclipse\\src\\main\\webapp\\download\\305");
//		System.out.println(herfs.size());
//		System.out.println("successful");
//		String regex = "^[\\d]+\\-[\\d].[a-z]{3}";
//		String regex1 = "^([\\d]|-)+.[a-z]{3}";
//		Pattern compile = Pattern.compile(regex1);
//		Matcher matcher = compile.matcher("2017.pdf");
//		if(matcher.find()) {
//			System.out.println("匹配");
//		}else {
//			System.out.println("不匹配");
//		}
//		System.out.println(new File("").getAbsolutePath());
//		System.out.println(ConstPath.getBase_Path());
		URL url = DownloadUtils.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		filePath = URLDecoder.decode(url.getPath(),"utf-8");
		System.out.println(filePath);
	}
}
