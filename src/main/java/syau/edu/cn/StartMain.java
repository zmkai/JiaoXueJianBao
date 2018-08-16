package syau.edu.cn;

import java.io.File;
import java.io.IOException;

public class StartMain {

	public static void main(String[] args) {
//		System.out.println(System.getProperty("user.dir"));
//		String path = System.getProperty("user.dir");
		String path = "../../";
		new ConstPath(path);
		try {
			JsoupUtils.connection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
