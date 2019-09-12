package com.jac.picplace.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jac.picplace.controller.viewstate.NavState;



@Controller
public class MainController {

	public MainController() {
		// TODO Auto-generated constructor stub
	}

	   
	   private String getUsername() {
		   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		   return auth == null ? null : auth.getName();
	   }
	   

	   @Autowired NavState navState;
	   
	   @RequestMapping("/")
	   public String viewHome(Model model, Pageable page, HttpSession session) {
		   String username = getUsername();
		   if(username != null) {
		      //Page<Photo> thumbnails = photoService.getThumbnails(username, page);
		     
		      model.addAttribute("username", username);

			  model.addAttribute("navState", navState);
			 /*
			  model.addAttribute("thumbnails", thumbnails);
			  model.addAttribute("highestPageIndex", thumbnails.getTotalPages() -1);
			  model.addAttribute("thumbnailDisplay", thumbnailDisplaySettings);
			  model.addAttribute("numberOfPhotosPerPage", photoService.getPhotosPerPage(page));
			  model.addAttribute("currentPage", page.getPageNumber());
			  viewState.assignState(thumbnails, page);
			  model.addAttribute("hasPrevious", page.hasPrevious());		  
			  processCounter(session, model, username);
		   */
		   }
	      return "welcome";
	   }
	
	
}


