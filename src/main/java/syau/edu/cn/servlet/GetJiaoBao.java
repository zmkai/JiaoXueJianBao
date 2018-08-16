package syau.edu.cn.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;

import syau.edu.cn.ConstPath;
import syau.edu.cn.JsonUtils;
import syau.edu.cn.JsoupUtils;
import syau.edu.cn.pojo.MyFile;

/**
 * Servlet implementation class GetJiaoBao
 */
public class GetJiaoBao extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(request.getServletContext().getRealPath("/"));
		String rootpath = request.getServletContext().getRealPath("/");
		new ConstPath(rootpath);
//		String path = request.getServletContext().getContextPath().replaceAll("/", "");
//		File file = new File(path);
//		System.out.println(file.getPath());
//		System.out.println(file.getParent());
		

		List<MyFile> lists = JsoupUtils.connection();
		//System.out.println(lists.size());
		String result = "";
		if(lists!=null&&lists.size()>0) {
			result = JsonUtils.JsonResponse(0, "successful", lists);
		}
		response.getWriter().write(result);
	}

}
