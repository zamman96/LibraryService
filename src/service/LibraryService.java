package service;

import java.util.List;
import java.util.Map;

import controller.MainController;
import dao.LibraryDao;
import util.JDBCUtil;

public class LibraryService {

	private static LibraryService instance;

	private LibraryService() {

	}

	public static LibraryService getInstance() {
		if (instance == null) {
			instance = new LibraryService();
		}
		return instance;
	}
	
	LibraryDao libdao = LibraryDao.getInstance();
	
	// 지역구 이름 출력
	public List<Map<String,Object>> localName(){
		return libdao.localName();
	}
	
	// 지역선택 시 지역에 맞는 도서관 출럭
	/**
	 * @LOC_NO
	 * @return
	 */
	public List<Map<String,Object>> localLibraryList(List<Object> param){
		return libdao.localLibraryList(param);
	}
	
	// 전체 도서관 출력
	public List<Map<String,Object>> librarylist(){
		return libdao.librarylist();
	}
	
	/**
	 * @param libNo
	 * sessionStorage에 library정보 저장
	 */
	public void librarySel(int libNo){
		Map<String, Object> library = libdao.librarySel(libNo);
		MainController.sessionStorage.remove("library");
		MainController.sessionStorage.put("library", library);
	}
	
	public List<Map<String,Object>> searchLibrary(String input) {
		return libdao.searchLibrary(input);
	}
}
