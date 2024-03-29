package com.cafeapp.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cafeapp.dto.menu.MenuFile;
import com.cafeapp.dto.menu.MenuList;
import com.cafeapp.dto.menu.MenuSearchCondition;
import com.cafeapp.dto.order.OrderList;
import com.cafeapp.dto.product.ProductFile;
import com.cafeapp.dto.product.ProductList;
import com.cafeapp.dto.product.ProductSearchCondition;
import com.cafeapp.dto.util.FileInfo;
import com.cafeapp.service.menu.MenuService;
import com.cafeapp.service.product.ProductService;
import com.cafeapp.util.FileManager;

@Controller
public class AdminMenuController {

	@Autowired
	MenuService menuService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	FileManager fileManager;
	
	//음료 리스트 및 검색
	@RequestMapping("/admin/drinkList")
	public String drinkList(Model model, @ModelAttribute MenuSearchCondition menuSearchCondition) {
		
		System.out.println(menuSearchCondition);
		
		List<MenuList> menuList = menuService.findDrinkBySearchCondition(menuSearchCondition); //검색
		
	    // 각 메뉴 항목에 대해 전체 파일 경로를 설정
	    menuList.forEach(menu -> {
	        String fullFilePath = menu.getFilePath() + menu.getFileName();
	        menu.setFullFilePath(fullFilePath); // MenuList 클래스에 fullFilePath 필드가 있다고 가정
	    });
			
		model.addAttribute("menuList", menuList);
		model.addAttribute("totalMenuList", menuList.size()); //총 음료갯수
		
		return "admin/adminDrinkList";
	}
	
	//음료 신규추가
	@GetMapping("/admin/drinkRegister")
	public String drinkRegister() {
		return "/admin/adminDrinkRegister";
	}
	
	@PostMapping("/admin/drinkRegister")
	@Transactional // 메소드 전체를 하나의 트랜잭션으로 관리하도록 하여, 중간에 실패하는 경우 모든 데이터베이스 작업을 롤백
	public String drinkRegisterProcess(MenuFile menuFile) {
		
		System.out.println(menuFile);
		
		try {
//			FileManager fileManager = new FileManager(); -> 위에서 @Autowired 해서 안써줘도됨!		
			// 1. 파일정보 추출 + 파일 실제 폴더에 저장
			FileInfo fileInfo = fileManager.storeFile(menuFile.getProfileImage(), "drink/");
			
			// 2. DB에 파일정보 저장
			int result = menuService.saveFileInfo(fileInfo); //이때 파일id생김-> 시퀀스
			
			// 파일 저장 실패? -> 그냥 다 실패
			// 파일 저장 실패? -> 첨부파일 없는걸로 치고, 프로파일 정보만 저장!
			
			if (result > 0) {
				
				//DB에 저장된 fileName를 가져오기 위해..
				FileInfo savedFileInfo = menuService.findFileInfoByFileName(fileInfo.getFileName());
				
				
				// 3. UserProfile 정보 저장
				MenuList menuList = new MenuList();
				menuList.setMenuType( menuFile.getMenuType() );
				menuList.setMenuName( menuFile.getMenuName() );
				menuList.setMenuDescription( menuFile.getMenuDescription() );
				menuList.setMenuSize( menuFile.getMenuSize() );
				menuList.setMenuCalories( menuFile.getMenuCalories() );
				menuList.setMenuAllergyInfo( menuFile.getMenuAllergyInfo() );
				menuList.setMenuSaturatedFat( menuFile.getMenuSaturatedFat() );
				menuList.setMenuSugars(menuFile.getMenuSugars());
				menuList.setMenuSodium(menuFile.getMenuSodium());
				menuList.setMenuProtein(menuFile.getMenuProtein());
				menuList.setMenuCaffeine(menuFile.getMenuCaffeine());			
				
				//DB에 저장된 fileId를 가져와서 세팅!
				menuList.setFileId(savedFileInfo.getFileId());
				int result2 = menuService.saveDrinkList(menuList);
				
	            if (result2 > 0) {
	                return "redirect:/admin/drinkList";
	                
	            } else {
	            	// 데이터 저장 실패 시
	                return "redirect:/admin/drinkRegister";
	            }
			} else {
	            // 파일 저장 실패 시
	            return "redirect:/admin/drinkRegister";
	        }
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return "redirect:/admin/drinkRegister";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return "redirect:/admin/drinkRegister";
		}
		
	}
	
	//음료 정보 수정
	@GetMapping("/admin/drinkModify")
	public String drinkModify(@RequestParam String menuNumber, Model model) {
		
		int intMenuNumber = Integer.parseInt(menuNumber);

		MenuList menuList = menuService.findMenuByMenuNumber(intMenuNumber);
		System.out.println(menuList);
		
		model.addAttribute("menuList", menuList);
		
		return "/admin/adminDrinkModify";
	}
	
	@PostMapping("/admin/drinkModify")
	public String drinkModifyProcess(MenuFile menuFile, @RequestParam int menuNumber, RedirectAttributes redirectAttributes) {
	    MenuList menuList = new MenuList();
	    menuList.setMenuNumber(menuNumber);
	    menuList.setMenuType(menuFile.getMenuType());
	    menuList.setMenuName(menuFile.getMenuName());
	    menuList.setMenuDescription(menuFile.getMenuDescription());
	    menuList.setMenuSize(menuFile.getMenuSize());
	    menuList.setMenuCalories(menuFile.getMenuCalories());
	    menuList.setMenuAllergyInfo(menuFile.getMenuAllergyInfo());
	    menuList.setMenuSaturatedFat(menuFile.getMenuSaturatedFat());
	    menuList.setMenuSugars(menuFile.getMenuSugars());
	    menuList.setMenuSodium(menuFile.getMenuSodium());
	    menuList.setMenuProtein(menuFile.getMenuProtein());
	    menuList.setMenuCaffeine(menuFile.getMenuCaffeine());

	    try {
	        // 파일이 존재하는 경우에만 파일 저장 로직을 수행합니다.
	        if (!menuFile.getProfileImage().isEmpty()) {
	            FileInfo fileInfo = fileManager.storeFile(menuFile.getProfileImage(), "drink/");
	            int result = menuService.saveFileInfo(fileInfo);
	            
	            if (result > 0) {
	                FileInfo savedFileInfo = menuService.findFileInfoByFileName(fileInfo.getFileName());
	                menuList.setFileId(savedFileInfo.getFileId()); // 새로운 파일 ID 세팅
	            } else {
	                redirectAttributes.addFlashAttribute("errorMessage", "파일 저장에 실패하였습니다.");
	                return "redirect:/admin/drinkModify";
	            }
	        } else {
	            // 새 파일이 업로드되지 않았다면 기존의 fileId를 사용합니다.
	            menuList.setFileId(menuFile.getPreviousFileId());
	        }

	        // 메뉴 정보를 업데이트하는 부분
	        int result2 = menuService.modifyMenu(menuList);
	        
	        if (result2 > 0) {
	            redirectAttributes.addFlashAttribute("successMessage", "수정되었습니다.");
	            return "redirect:/admin/drinkList";
	        } else {
	            // 데이터 업데이트 실패
	            redirectAttributes.addFlashAttribute("errorMessage", "수정에 실패하였습니다.");
	            return "redirect:/admin/drinkModify";
	        }
	    } catch (IllegalStateException | IOException e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("errorMessage", "수정에 실패하였습니다.");
	        return "redirect:/admin/drinkModify";
	    }
	}
	
	//음료 삭제
	@PostMapping("/admin/drinkRemove")
	public String drinkRemove(@RequestParam List<String> menuNumber) {

		System.out.println(menuNumber);

		// String 리스트를 Integer 리스트로 변환
	    List<Integer> numbers = menuNumber.stream()
                                           .map(Integer::parseInt)
                                           .collect(Collectors.toList());
	    
		menuService.removeMenu(numbers);
		
		return "redirect:/admin/drinkList";
	}
	
//	====================================================
	//푸드 리스트 및 검색
	@RequestMapping("/admin/foodList")
	public String foodList(Model model, @ModelAttribute MenuSearchCondition menuSearchCondition) {
		
		System.out.println(menuSearchCondition);
		
		List<MenuList> menuList = menuService.findFoodBySearchCondition(menuSearchCondition); //검색
		
	    // 각 메뉴 항목에 대해 전체 파일 경로를 설정
	    menuList.forEach(menu -> {
	        String fullFilePath = menu.getFilePath() + menu.getFileName();
	        menu.setFullFilePath(fullFilePath); // MenuList 클래스에 fullFilePath 필드가 있다고 가정
	    });
			
		model.addAttribute("menuList", menuList);
		model.addAttribute("totalMenuList", menuList.size()); //총 음료갯수
		
		return "admin/adminFoodList";
	}
	
	//푸드 신규추가
	@GetMapping("/admin/foodRegister")
	public String foodRegister() {
		return "/admin/adminFoodRegister";
	}
	
	@PostMapping("/admin/foodRegister")
	@Transactional // 메소드 전체를 하나의 트랜잭션으로 관리하도록 하여, 중간에 실패하는 경우 모든 데이터베이스 작업을 롤백
	public String foodRegisterProcess(MenuFile menuFile) {
		
		System.out.println(menuFile);
		
		try {
//			FileManager fileManager = new FileManager(); -> 위에서 @Autowired 해서 안써줘도됨!		
			// 1. 파일정보 추출 + 파일 실제 폴더에 저장
			FileInfo fileInfo = fileManager.storeFile(menuFile.getProfileImage(), "food/");
			
			// 2. DB에 파일정보 저장
			int result = menuService.saveFileInfo(fileInfo); //이때 파일id생김-> 시퀀스
			
			// 파일 저장 실패? -> 그냥 다 실패
			// 파일 저장 실패? -> 첨부파일 없는걸로 치고, 프로파일 정보만 저장!
			
			if (result > 0) {
				
				//DB에 저장된 fileName를 가져오기 위해..
				FileInfo savedFileInfo = menuService.findFileInfoByFileName(fileInfo.getFileName());
				
				
				// 3. UserProfile 정보 저장
				MenuList menuList = new MenuList();
				menuList.setMenuType( menuFile.getMenuType() );
				menuList.setMenuName( menuFile.getMenuName() );
				menuList.setMenuDescription( menuFile.getMenuDescription() );
				menuList.setMenuSize( menuFile.getMenuSize() );
				menuList.setMenuCalories( menuFile.getMenuCalories() );
				menuList.setMenuAllergyInfo( menuFile.getMenuAllergyInfo() );
				menuList.setMenuSaturatedFat( menuFile.getMenuSaturatedFat() );
				menuList.setMenuSugars(menuFile.getMenuSugars());
				menuList.setMenuSodium(menuFile.getMenuSodium());
				menuList.setMenuProtein(menuFile.getMenuProtein());
				menuList.setMenuCaffeine(menuFile.getMenuCaffeine());			
				
				//DB에 저장된 fileId를 가져와서 세팅!
				menuList.setFileId(savedFileInfo.getFileId());
				int result2 = menuService.saveFoodList(menuList);
				
	            if (result2 > 0) {
	                return "redirect:/admin/foodList";
	                
	            } else {
	            	// 데이터 저장 실패 시
	                return "redirect:/admin/foodRegister";
	            }
			} else {
	            // 파일 저장 실패 시
	            return "redirect:/admin/foodRegister";
	        }
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return "redirect:/admin/foodRegister";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return "redirect:/admin/foodRegister";
		}
		
	}

	//푸드 정보 수정
	@GetMapping("/admin/foodModify")
	public String foodModify(@RequestParam String menuNumber, Model model) {
		
		int intMenuNumber = Integer.parseInt(menuNumber);

		MenuList menuList = menuService.findMenuByMenuNumber(intMenuNumber);
		System.out.println(menuList);
		
		model.addAttribute("menuList", menuList);
		
		return "/admin/adminFoodModify";
	}
	
	@PostMapping("/admin/foodModify")
	public String foodModifyProcess(MenuFile menuFile, @RequestParam int menuNumber, RedirectAttributes redirectAttributes) {
	    MenuList menuList = new MenuList();
	    menuList.setMenuNumber(menuNumber);
	    menuList.setMenuType(menuFile.getMenuType());
	    menuList.setMenuName(menuFile.getMenuName());
	    menuList.setMenuDescription(menuFile.getMenuDescription());
	    menuList.setMenuSize(menuFile.getMenuSize());
	    menuList.setMenuCalories(menuFile.getMenuCalories());
	    menuList.setMenuAllergyInfo(menuFile.getMenuAllergyInfo());
	    menuList.setMenuSaturatedFat(menuFile.getMenuSaturatedFat());
	    menuList.setMenuSugars(menuFile.getMenuSugars());
	    menuList.setMenuSodium(menuFile.getMenuSodium());
	    menuList.setMenuProtein(menuFile.getMenuProtein());
	    menuList.setMenuCaffeine(menuFile.getMenuCaffeine());

	    try {
	        // 파일이 존재하는 경우에만 파일 저장 로직을 수행합니다.
	        if (!menuFile.getProfileImage().isEmpty()) {
	            FileInfo fileInfo = fileManager.storeFile(menuFile.getProfileImage(), "food/");
	            int result = menuService.saveFileInfo(fileInfo);
	            
	            if (result > 0) {
	                FileInfo savedFileInfo = menuService.findFileInfoByFileName(fileInfo.getFileName());
	                menuList.setFileId(savedFileInfo.getFileId()); // 새로운 파일 ID 세팅
	            } else {
	                redirectAttributes.addFlashAttribute("errorMessage", "파일 저장에 실패하였습니다.");
	                return "redirect:/admin/foodModify";
	            }
	        } else {
	            // 새 파일이 업로드되지 않았다면 기존의 fileId를 사용합니다.
	            menuList.setFileId(menuFile.getPreviousFileId());
	        }

	        // 메뉴 정보를 업데이트하는 부분
	        int result2 = menuService.modifyMenu(menuList);
	        
	        if (result2 > 0) {
	            redirectAttributes.addFlashAttribute("successMessage", "수정되었습니다.");
	            return "redirect:/admin/foodList";
	        } else {
	            // 데이터 업데이트 실패
	            redirectAttributes.addFlashAttribute("errorMessage", "수정에 실패하였습니다.");
	            return "redirect:/admin/foodModify";
	        }
	    } catch (IllegalStateException | IOException e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("errorMessage", "수정에 실패하였습니다.");
	        return "redirect:/admin/foodModify";
	    }
	}
	
	//푸드 삭제
	@PostMapping("/admin/foodRemove")
	public String foodRemove(@RequestParam List<String> menuNumber) {

		System.out.println(menuNumber);

		// String 리스트를 Integer 리스트로 변환
	    List<Integer> numbers = menuNumber.stream()
                                           .map(Integer::parseInt)
                                           .collect(Collectors.toList());
	    
		menuService.removeMenu(numbers);
		
		return "redirect:/admin/foodList";
	}

//	=======================================================================
	//상품리스트 목록 및 검색
	@RequestMapping("/admin/productList")
	public String productList(Model model, @ModelAttribute ProductSearchCondition productSearchCondition) {
		
		System.out.println(productSearchCondition);
		
		List<ProductList> productList = menuService.findProductBySearchCondition(productSearchCondition); //검색
		
	    // 각 메뉴 항목에 대해 전체 파일 경로를 설정
		productList.forEach(product -> {
	        String fullFilePath = product.getFilePath() + product.getFileName();
	        product.setFullFilePath(fullFilePath); // MenuList 클래스에 fullFilePath 필드가 있다고 가정
	    });
			
		model.addAttribute("productList", productList);
		model.addAttribute("totalProductList", productList.size()); //총 음료갯수
		
		return "admin/adminProductList";
	}
	
	//상품 신규추가
	@GetMapping("/admin/productRegister")
	public String productRegister() {
		return "/admin/adminProductRegister";
	}
	
	@PostMapping("/admin/productRegister")
	@Transactional // 메소드 전체를 하나의 트랜잭션으로 관리하도록 하여, 중간에 실패하는 경우 모든 데이터베이스 작업을 롤백
	public String productRegisterProcess(ProductFile productFile) {
		
		System.out.println(productFile);
		
		try {
//			FileManager fileManager = new FileManager(); -> 위에서 @Autowired 해서 안써줘도됨!		
			// 1. 파일정보 추출 + 파일 실제 폴더에 저장
			FileInfo fileInfo = fileManager.storeFile(productFile.getProfileImage(), "product/");
			
			// 2. DB에 파일정보 저장
			int result = menuService.saveFileInfo(fileInfo); //이때 파일id생김-> 시퀀스
			
			// 파일 저장 실패? -> 그냥 다 실패
			// 파일 저장 실패? -> 첨부파일 없는걸로 치고, 프로파일 정보만 저장!
			
			if (result > 0) {
				
				//DB에 저장된 fileName를 가져오기 위해..
				FileInfo savedFileInfo = menuService.findFileInfoByFileName(fileInfo.getFileName());
				
				
				// 3. UserProfile 정보 저장
				ProductList productList = new ProductList();
				productList.setProductType( productFile.getProductType() );
				productList.setProductName( productFile.getProductName() );
				productList.setProductDescription( productFile.getProductDescription() );
				productList.setProductPrice( productFile.getProductPrice() );
				productList.setProductSize( productFile.getProductSize() );
				productList.setProductStockQuantity( productFile.getProductStockQuantity() );
				productList.setProductStatus( productFile.getProductStatus() );		
				
				//DB에 저장된 fileId를 가져와서 세팅!
				productList.setFileId(savedFileInfo.getFileId());
				int result2 = menuService.saveProductList(productList);
				
	            if (result2 > 0) {
	                return "redirect:/admin/productList";
	                
	            } else {
	            	// 데이터 저장 실패 시
	                return "redirect:/admin/productRegister";
	            }
			} else {
	            // 파일 저장 실패 시
	            return "redirect:/admin/productRegister";
	        }
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return "redirect:/admin/productRegister";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return "redirect:/admin/productRegister";
		}
		
	}

	//상품 정보 수정
	@GetMapping("/admin/productModify")
	public String productModify(@RequestParam String productNumber, Model model) {
		
		int intProductNumber = Integer.parseInt(productNumber);

		ProductList productList = menuService.findProductByProductNumber(intProductNumber);
		System.out.println(productList);
		
		model.addAttribute("productList", productList);
		
		return "/admin/adminProductModify";
	}
	
	@PostMapping("/admin/productModify")
	public String productModifyProcess(ProductFile productFile, @RequestParam int productNumber, RedirectAttributes redirectAttributes) {
		ProductList productList = new ProductList();
		productList.setProductNumber(productNumber);
		productList.setProductType(productFile.getProductType());
		productList.setProductName(productFile.getProductName());
		productList.setProductDescription(productFile.getProductDescription());
		productList.setProductPrice(productFile.getProductPrice());
		productList.setProductSize(productFile.getProductSize());
		productList.setProductStockQuantity(productFile.getProductStockQuantity());
		productList.setProductStatus(productFile.getProductStatus());

	    try {
	        // 파일이 존재하는 경우에만 파일 저장 로직을 수행합니다.
	        if (!productFile.getProfileImage().isEmpty()) {
	            FileInfo fileInfo = fileManager.storeFile(productFile.getProfileImage(), "product/");
	            int result = menuService.saveFileInfo(fileInfo);
	            
	            if (result > 0) {
	                FileInfo savedFileInfo = menuService.findFileInfoByFileName(fileInfo.getFileName());
	                productList.setFileId(savedFileInfo.getFileId()); // 새로운 파일 ID 세팅
	            } else {
	                redirectAttributes.addFlashAttribute("errorMessage", "파일 저장에 실패하였습니다.");
	                return "redirect:/admin/productModify";
	            }
	        } else {
	            // 새 파일이 업로드되지 않았다면 기존의 fileId를 사용합니다.
	        	productList.setFileId(productFile.getPreviousFileId());
	        }

	        // 메뉴 정보를 업데이트하는 부분
	        int result2 = menuService.modifyProduct(productList);
	        
	        if (result2 > 0) {
	            redirectAttributes.addFlashAttribute("successMessage", "수정되었습니다.");
	            return "redirect:/admin/productList";
	        } else {
	            // 데이터 업데이트 실패
	            redirectAttributes.addFlashAttribute("errorMessage", "수정에 실패하였습니다.");
	            return "redirect:/admin/productModify";
	        }
	    } catch (IllegalStateException | IOException e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("errorMessage", "수정에 실패하였습니다.");
	        return "redirect:/admin/productModify";
	    }
	}

	//상품 삭제
	@PostMapping("/admin/productRemove")
	public String productRemove(@RequestParam List<String> productNumber) {

		System.out.println(productNumber);

		// String 리스트를 Integer 리스트로 변환
	    List<Integer> numbers = productNumber.stream()
                                           .map(Integer::parseInt)
                                           .collect(Collectors.toList());
	    
		menuService.removeProduct(numbers);
		
		return "redirect:/admin/productList";
	}

}
