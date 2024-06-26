package controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import print.Print;
import service.BookService;
import service.MemberService;
import service.PDSService;
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

	static public Map<String, Object> sessionStorage;
	MemberService memberService = MemberService.getInstance();
	BookService bookService = BookService.getInstance();
	PDSService pdsService = PDSService.getInstance();

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
		if (MainController.sessionStorage.containsKey("admin")
				|| MainController.sessionStorage.containsKey("manager")) {
			return View.ADMIN;
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
		String name = ScanUtil.nextLine(tap + "이름  >  ");
		System.out.println(var);
		System.out.println(tap + "\t\t전화번호는 숫자만 입력해주세요");
		System.out.println(tap + "\t\t예시 : 01012341234");
		System.out.println(var);
		System.out.println();
		String tel = "";
		while (true) {
			tel = ScanUtil.nextLine(tap + "전화번호  > ");
			if (tel.matches("\\d+")) {
				break;
			}
			noticeNotNo();
		}
		String num1 = tel.substring(0, 3);
		String num2 = tel.substring(3, 7);
		String num3 = tel.substring(7);
		tel = num1 + "-" + num2 + "-" + num3;
		String id = memberService.findId(name, tel);
		if (id != null) {
			if (id.length() > 3) {
				String truncatedId = id.substring(0, id.length() - 3);
				System.out.println(tap + "회원님의 아이디는 " + truncatedId + "***" + "입니다.");
			}
		} else {
			System.out.println(RED + "\t" + var + END);
			System.out.println(tap + "\t\t일치하는 회원 정보가 없습니다.");
			System.out.println(RED + "\t" + var + END);
		}

		return mainMenu();
	}

	// 비번 찾기
	public View pwfound() {
		String id = ScanUtil.nextLine(tap + "아이디 >  ");
		String name = ScanUtil.nextLine(tap + "이름  >  ");
		System.out.println(var);
		System.out.println(tap + "\t\t전화번호는 숫자만 입력해주세요");
		System.out.println(tap + "\t\t예시 : 01012341234");
		System.out.println(var);
		System.out.println();
		String tel = "";
		while (true) {
			tel = ScanUtil.nextLine(tap + "전화번호  > ");
			if (tel.matches("\\d+")) {
				break;
			}
			noticeNotNo();
		}
		String num1 = tel.substring(0, 3);
		String num2 = tel.substring(3, 7);
		String num3 = tel.substring(7);
		tel = num1 + "-" + num2 + "-" + num3;
		List<Object> param = new ArrayList<Object>();
		param.add(id);
		param.add(name);
		param.add(tel);

		Map<String, Object> map = memberService.findPassword(param);
		if (map == null) {
			System.out.println(RED + "\t" + var + END);
			System.out.println(tap + "\t\t일치하는 회원 정보가 없습니다");
			System.out.println(RED + "\t" + var + END);
			return mainMenu();
		}

		int memNo = ((BigDecimal) map.get("MEM_NO")).intValue();
		MainController.sessionStorage.put("found", memNo);
		return View.UPDATE;
	}

	public View sign() {
		printMenuOverVar();
		System.out.println(tap+"회원 가입");
		printMenuVar();
		while (true) {
			String id = "";
			boolean idCheck = false;
			do {
				id = ScanUtil.nextLine(tap + "아이디   > ");
				// 아이디 길이 및 형식 검사
				if (isValidId(id)) {
					List<Object> idList = new ArrayList<>();
					idList.add(id);
					idCheck = memberService.idcheck(idList);
				} else {
					System.out.println(tap + "아이디는 5자 이상 15자 이하여야 합니다.");
				}
			} while (!idCheck);

			String pw = "";
			// 비밀번호 길이 검사
			while (true) {
				pw = ScanUtil.nextLine(tap + "비밀번호  >");
				if (isValidPassword(pw)) {
					break;
				} else {
					System.out.println(tap + "비밀번호는 5자 이상 15자 이하여야 합니다.");
				}
			}

			String nm = "";
			// 이름 길이 및 한글 여부 검사
			while (true) {
				nm = ScanUtil.nextLine(tap + "이름  > ");
				if (isValidName(nm)) {
					break;
				} else {
					System.out.println(tap + "이름은 10자 이내의 한글로 입력해야 합니다.");
				}
			}

			System.out.println(var);
			System.out.println(tap + "\t\t전화번호는 숫자만 입력해주세요");
			System.out.println(tap + "\t\t예시 : 01012341234");
			System.out.println(var);
			System.out.println();

			String tel = "";
			// 전화번호 유효성 검사 및 형식 변환
			while (true) {
				tel = ScanUtil.nextLine(tap + "전화번호  > ");
				if (isValidPhoneNumber(tel)) {
					break;
				} else {
					System.out.println(tap + "전화번호 형식이 잘못되었습니다.");
				}
			}
			tel = formatPhoneNumber(tel);

			List<Object> param = new ArrayList<Object>();
			param.add(id);
			param.add(pw);
			boolean loginChk = memberService.login(param); // 세션에 정보저장
			param.add(nm);
			param.add(tel);
			memberService.sign(param);
			System.out.println(tap + "회원가입이 완료되었습니다.");
			System.out.println();
			return mainMenu();
		}
	}

	// 아이디 유효성 검사
	public boolean isValidId(String id) {
		return id.length() >= 5 && id.length() <= 15 && id.matches("^[a-z0-9]*$");
	}

	// 비밀번호 유효성 검사
	public boolean isValidPassword(String pw) {
		return pw.length() >= 5 && pw.length() <= 15;
	}

	// 이름 유효성 검사
	public boolean isValidName(String nm) {
		return nm.length() <= 10 && nm.matches("^[가-힣]*$");
	}

	// 전화번호 유효성 검사
	public boolean isValidPhoneNumber(String tel) {
		return tel.matches("\\d{11}");
	}

	// 전화번호 형식 변환
	public String formatPhoneNumber(String tel) {
		return tel.substring(0, 3) + "-" + tel.substring(3, 7) + "-" + tel.substring(7);
	}

	// 로그인
	public View login() {
		printMenuOverVar();
		System.out.println(tap+"로그인");
		printMenuVar();
		String id = ScanUtil.nextLine(tap + "아이디  > ");
		String pw = ScanUtil.nextLine(tap + "비밀번호  > ");
		List<Object> param = new ArrayList();
		param.add(id);
		param.add(pw);
		boolean loginchk = memberService.login(param);
		if (loginchk) {
			Map<String, Object> member = (Map<String, Object>) MainController.sessionStorage.get("member");
			System.out.println(var);
			System.out.println(notice + "\t" + member.get("MEM_NAME") + "님 환영합니다");
			System.out.println(var);
			System.out.println();
			int no = ((BigDecimal) member.get("ADMIN_NO")).intValue();
			if (no == 2) {
				MainController.sessionStorage.put("admin", member);
				return View.ADMIN;
			} else if (no == 3) {
				MainController.sessionStorage.put("manager", member);
				return View.ADMIN;
			}
		} else if (!loginchk) {
			printMenuVar();
			System.out.println(tap + "1. 재로그인\t\t2. 회원가입\t\t3. 아이디 찾기");
			printMenuVar();
			int sel = ScanUtil.menu();

			switch (sel) {
			case 1:
				return View.LOGIN;
			case 2:
				return View.SIGN;
			default:
				return View.IDFOUND;
			}
		}
		List<Object> no = memberNo();
		// 대출예약 기간 지난 리스트가 있으면 출력
		boolean bookTimeOverChk = bookService.refTimeOverChk(no);
		if (bookTimeOverChk) {
			System.out.println(RED + var + END);
			System.out.println(notice + "\t대출예약기간이 지난 도서가 있습니다");
			System.out.println(RED + var + END);
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
			System.out.println(var);
			System.out.println(notice + "  대출 가능한 예약도서가 있습니다."); // 알림창
			System.out.println(var);
			return View.BOOK_RESERVATION_LIST;
		}
		if (MainController.sessionStorage.containsKey("View")) {
			View view = (View) MainController.sessionStorage.remove("View");
			return view;
		}
		return mainMenu();
	}

	public View logout() {
		memberService.logout();
		return View.MAIN;
	}

	/**
	 * @return
	 */
	public View delete() {

		System.out.println(tap + "비밀번호를 입력해 주세요");
		String inputPassword = ScanUtil.menuStr();
		// 세션에서 회원 정보 가져오기
		Map<String, Object> member = (Map<String, Object>) MainController.sessionStorage.get("member");

		// 회원 번호 가져오기
		BigDecimal no = (BigDecimal) member.get("MEM_NO");
		String storedPassword = (String) member.get("MEM_PASS");
		int memNo = no.intValue();

		boolean isPasswordCorrect = memberService.checkPw("", inputPassword);

		// 회원이 빌린 도서가 있는지 확인
		List<Object> param = new ArrayList<>();
		param.add(memNo);
		List<Map<String, Object>> rentBooks = memberService.mem_book_rent(param);

		// 빌린 도서가 있는 경우 탈퇴 불가 안내 메시지 출력
		if (rentBooks != null && !rentBooks.isEmpty()) {
			System.out.println(RED + "\t" + var + END);
			System.out.println(tap + "\t\t탈퇴할 수 없습니다: 대출 도서가 있습니다.");
			System.out.println(RED + "\t" + var + END);
			return View.MYPAGE;
			// 탈퇴 불가 시 에러 뷰를 반환하거나, 적절한 처리를 수행합니다.
		}
		
		List<Map<String,Object>> list = bookService.bookRefList(param);
		bookService.bookRefCancelAll(param);
		// 다음 순번 사람에게 순번
		if(list!=null) {
		for(Map<String, Object> map : list) {
			String bookNo = (String) map.get("BOOK_NO");
			bookService.updateRefDate(bookNo);
		}
		}
		// 도서예약취소
		memberService.delete(param);
		// 자료실 예약취소
		pdsService.pdsResDelete(memNo);
		System.out.println();
		// 세션 클리어
		MainController.sessionStorage.remove("member");
		MainController.sessionStorage.remove("admin");
		MainController.sessionStorage.remove("manager");
		return mainMenu();
	}

	public View update() {
		int sel = 0;
		int memNo = 0;
		String id = "";
		String newPassword = "";
		String inputPassword="";
		if (!MainController.sessionStorage.containsKey("found")) {
			System.out.println(tap + "비밀번호를 입력해 주세요");
			inputPassword = ScanUtil.menuStr();

			// 현재 세션에 저장된 회원 정보 가져오기
			Map<String, Object> member = (Map<String, Object>) MainController.sessionStorage.get("member");
			String storedPassword = (String) member.get("MEM_PASS"); // 현재 세션에 저장된 비밀번호 가져오기
			memNo = ((BigDecimal) member.get("MEM_NO")).intValue();
			id = (String) member.get("MEM_ID");

			// 비밀번호 일치 여부 확인
			boolean isPasswordCorrect = memberService.checkPw("", inputPassword);

			if (isPasswordCorrect) {
				System.out.println(var);
				System.out.println(notice + "   비밀번호가 일치합니다.");
				System.out.println(var);
				System.out.println();

			} else {
				System.out.println(var);
				System.out.println(tap + "\t\t비밀번호가 일치하지 않습니다.");
				System.out.println(tap + "\t\t마이페이지로 돌아갑니다");
				System.out.println(var);
				if (MainController.sessionStorage.containsKey("admin")
						|| MainController.sessionStorage.containsKey("manager")) {
					return View.ADMIN_PAGE;
				} else {
					return View.MYPAGE;
				}
			}
			printMenuVar();
			System.out.println(tap + "수정하실 정보를 선택해 주세요");
			System.out.println(tap + "1. 비밀번호 수정\t\t2. 전화 번호 수정 \t\t3. 전체 수정");
			printMenuVar();
			sel = ScanUtil.menu();
		} else {
			memNo = (int) MainController.sessionStorage.remove("found");
			sel = 1; // found
		}
		List<Object> param = new ArrayList<>();
		if (sel == 1 || sel == 3) {
			System.out.println(tap + "새로운 비밀번호를 입력하세요:");
			newPassword = ScanUtil.nextLine(tap + "비밀번호       > ");
			param.add(newPassword);

		}
		if (sel == 2 || sel == 3) {
			System.out.println(var);
			System.out.println(tap + "\t\t전화번호는 숫자만 입력해주세요");
			System.out.println(tap + "\t\t예시 : 01012341234");
			System.out.println(var);
			System.out.println();
			System.out.println(var);
			System.out.println(tap + "\t\t새로운 전화번호를 입력하세요:");
			System.out.println(var);
			String newPhoneNumber = "";
			while (true) {
				newPhoneNumber = ScanUtil.nextLine(tap + "전화번호  > ");
				if (newPhoneNumber.matches("\\d+")) {
					if (newPhoneNumber.length() == 11) {
						String num1 = newPhoneNumber.substring(0, 3);
						String num2 = newPhoneNumber.substring(3, 7);
						String num3 = newPhoneNumber.substring(7);
						newPhoneNumber = num1 + "-" + num2 + "-" + num3;
						param.add(newPhoneNumber);
						break;
					} else {
						System.out.println(RED + var + END);
						System.out.println(tap + "\t\t전화번호는 11자리여야 합니다. 다시 입력해주세요.");
						System.out.println(RED + var + END);
					}
				} else {
					System.out.println(RED + var + END);
					System.out.println(tap + "\t\t전화번호는 숫자만 입력해주세요. 다시 입력해주세요.");
					System.out.println(RED + var + END);
				}
			}
		}
		param.add(memNo);
		memberService.update(param, sel);
		System.out.println(var);
		System.out.println(notice + "   정보가 수정되었습니다.");
		System.out.println(var);
		if (MainController.sessionStorage.containsKey("member")) {
			List<Object> log = new ArrayList<Object>();
			log.add(id);
			if(newPassword.isEmpty()) {
				log.add(inputPassword);
			}else {
				log.add(newPassword);}
			memberService.save(log);
			if (MainController.sessionStorage.containsKey("manager")
					|| MainController.sessionStorage.containsKey("admin")) {
				return View.ADMIN_PAGE;
			}
			return View.MYPAGE;
		} else {
			return mainMenu();
		}

	}

}
