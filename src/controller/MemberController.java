package controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import print.Print;
import service.BookService;
import service.MemberService;
import util.ScanUtil;
import util.View;

public class MemberController extends Print {
	private static MemberController instance;

	private MemberController() {

	}

	public static MemberController getInstance() {
		if (instance == null) {
			instance = new MemberController();
		}
		return instance;
	}

	static public Map<String, Object> sessionStorage = new HashMap<>();
	MemberService memberService = MemberService.getInstance();
	BookService bookService = BookService.getInstance();

	/**
	 * @return 로그인과 도서관 선택에 따라 main화면이 다름
	 */
	public View mainMenu() {
		if (MainController.sessionStorage.containsKey("member")
				&& MainController.sessionStorage.containsKey("library")) {
			return View.MAIN_ALL;
		}
		if (MainController.sessionStorage.containsKey("member")) {
			return View.MAIN_MEMBER;
		}
		if (MainController.sessionStorage.containsKey("library")) {
			return View.MAIN_LIBRARY;
		}
		return View.MAIN;
	}

	/**
	 * @return mem_no정보를 담고있는 param
	 */
	public List<Object> memberNo() {
		List<Object> param = new ArrayList<>();
		Map<String, Object> member = (Map<String, Object>) MainController.sessionStorage.get("member");
		String no = "" + ((BigDecimal) member.get("MEM_NO")).intValue();
		param.add(no);
		return param;
	}

	// ID 이름 번호 받고
	// 3자리 뺴고 3자리는 *

	// pass id 이름 번호
	// 비번 수정

	/**
	 * <main> 로그인 비밀번호 회원가입 아이디 비밀번호 찾기 책 검색 자료실 좌석 조회
	 */

	// 아이디 찾기
	public View idfound() {
		String name = ScanUtil.nextLine("이름을 입력하세요 : ");
		String tel = ScanUtil.nextLine("전화번호를 입력하세요 : ");
		String id = memberService.findId(name, tel);
		if (id != null) {
			if (id.length() > 3) {
				String truncatedId = id.substring(0, id.length() - 3);
				System.out.println("회원님의 아이디는 " + truncatedId + "***" + "입니다.");
			}
		} else {
			System.out.println("일치하는 회원 정보가 없습니다.");
		}

		return mainMenu();
	}

	// 비번 찾기
	public View pwfound() {
		String name = ScanUtil.nextLine("이름을 입력하세요 : ");
		String id = ScanUtil.nextLine("아이디를 입력하세요 : ");
		String tel = ScanUtil.nextLine("전화번호를 입력하세요 : ");

		String password = memberService.findPassword(id, name, tel);
		if (password.length() > 3) {
			String truncatedPw = password.substring(0, id.length() - 3);
			System.out.println("회원님의 비밀번호는 " + truncatedPw + "***" + "입니다.");
		} else {
			System.out.println("일치하는 회원 정보가 없습니다.");
		}

		return mainMenu();
	}

	// 회원가입
	public View sign() {

	    while (true) {
	        String nm = ScanUtil.nextLine("NAME : ");
	        String id = ScanUtil.nextLine("ID: ");
	        String pw = ScanUtil.nextLine("PASS : ");
	        String tel = ScanUtil.nextLine("TELL : ");

	        List<Object> param = new ArrayList<Object>();
	        param.add(nm);
	        param.add(id);
	        param.add(pw);
	        param.add(tel);
	        
	        List<Object> idList = new ArrayList<Object>();
	        idList.add(id);
	        
	        boolean Idchk = memberService.idcheck(idList);


	        return View.SIGN;
	    }
	}

	// 로그인
	public View login() {
		String id = ScanUtil.nextLine("ID : ");
		String pw = ScanUtil.nextLine("pass : ");
		List<Object> param = new ArrayList();
		param.add(id);
		param.add(pw);
		boolean loginchk = memberService.login(param);
		if (loginchk) {
			Map<String, Object> member = (Map<String, Object>) MainController.sessionStorage.get("member");
			System.out.println(member.get("MEM_NAME") + "님 환영합니다");
		} else if (!loginchk) {
			System.out.println("1. 재로그인");
			System.out.println("2. 홈");
			int sel = ScanUtil.menu();

			switch (sel) {
			case 1:
				return View.LOGIN;
			case 2:
				return mainMenu();
			default:
				return View.LOGIN;
			}
		}
		
		List<Object> no = memberNo();
		// 대출예약 기간 지난 리스트가 있으면 출력
		boolean bookTimeOverChk = bookService.refTimeOverChk(no);
		if (bookTimeOverChk) {
			System.out.println("대출예약기간이 지난 도서가 있습니다");
			List<Map<String, Object>> list = bookService.refTimeOver(no); // 알림창
			printOverVar();
			printBookIndex();
			printMiddleVar();
			for (Map<String, Object> map : list) {
				printBookList(map);
			}
			printUnderVar();

			bookService.refTimeOverUpdate(no);
		}
		// 로그인 성공 시 대출 예약한 것이 대출이 가능한지 확인
		boolean bookRes = bookService.bookRefYN(no);
		if (bookRes) {
			System.out.println("대출 가능한 예약도서가 있습니다."); // 알림창
			return View.BOOK_RESERVATION_LIST;
		}
		if(MainController.sessionStorage.containsKey("View")) {
			View view = (View) MainController.sessionStorage.remove("View");
			return view;
		}
		return mainMenu();
	}
	
	
	public View logout() {
        memberService.logout(); 
        return View.MAIN;
    }
	
	
	
//	 public void updateUserInfo(String id, String password, String nickname, String phoneNumber) {
//    boolean isPasswordCorrect = mypageService.checkPw(id, password);
//    
//    if (isPasswordCorrect) {
//        mypageService.updateUserInfo(id, nickname, phoneNumber);
//        System.out.println("회원정보가 성공적으로 수정되었습니다.");
//    } else {
//        System.out.println("비밀번호가 일치하지 않습니다. 다시 시도해주세요.");
//    }
//    }
	

	public View delete() {
		Map<String,Object> 	member = (Map<String,Object>) sessionStorage.get("memveb");
		BigDecimal no = (BigDecimal) member.get("NO");
		int num = no.intValue();
		List<Object> param = new ArrayList();
		param.add(num);
		
		memberService.delete(param);
		sessionStorage.clear();
		
		return View.MAIN;
	}
	

}
