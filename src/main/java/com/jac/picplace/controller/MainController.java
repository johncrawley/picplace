package com.jac.picplace.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jac.picplace.controller.viewstate.NavState;
import com.jac.picplace.controller.viewstate.ThumbnailDisplaySettings;
import com.jac.picplace.controller.viewstate.ViewState;
import com.jac.picplace.form.PhotoForm;
import com.jac.picplace.model.photo.Photo;
import com.jac.picplace.service.PhotoService;



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
	   @Autowired ViewState viewState;
	   @Autowired private ThumbnailDisplaySettings thumbnailDisplaySettings;
	   
	   
	   @RequestMapping("/")
	   public String viewHome(Model model, Pageable page, HttpSession session) {
		   String username = getUsername();
		   if(username != null) {
		      Page<Photo> thumbnails = photoService.getThumbnails(username, page);
		     
		      model.addAttribute("username", username);
			  model.addAttribute("navState", navState);
			  model.addAttribute("thumbnails", thumbnails);
			  model.addAttribute("highestPageIndex", thumbnails.getTotalPages() -1);
			  model.addAttribute("thumbnailDisplay", thumbnailDisplaySettings);
			  model.addAttribute("numberOfPhotosPerPage", photoService.getPhotosPerPage(page));
			  model.addAttribute("currentPage", page.getPageNumber());
			  viewState.assignState(thumbnails, page);
			  model.addAttribute("hasPrevious", page.hasPrevious());		  
			  processCounter(session, model, username);
		   }
	      return "welcome";
	   }
	   

	   	private void processCounter(HttpSession session, Model model, String username) {
			  
		    int counter = 0;
			if(session.getAttribute("counter") != null) {
				counter = (Integer) session.getAttribute("counter");
			}
			System.out.println("counter: "+  counter);
			counter++;
			String counterStr = username + ": " + counter;
			 
			model.addAttribute("counter", counterStr );
			session.setAttribute("counter", counter);
	   	}
	
	  
	   @RequestMapping(value = "/upload", method=RequestMethod.GET)
	   public String uploadImagePage(Model model) {
		   PhotoForm photoForm = new PhotoForm();
		   model.addAttribute("photoForm", photoForm);
		   return "upload";
	   }

	   @Autowired
	   PhotoService photoService;

		@RequestMapping(value = "/upload", method=RequestMethod.POST)
		public ModelAndView uploadImage( @ModelAttribute PhotoForm photoForm) { //@ModelAttribute("photo" )) {
		
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String name = auth.getName();
			if(name == null) {
				return new ModelAndView("upload", "message", "Unable to Upload");
			}
			System.out.println("mainController uploadImage()  photo title: "+ photoForm.getTitle());
			photoForm.setUsername(name);
			photoService.addPhoto(photoForm);
			
			return new ModelAndView("upload", "message", "success");
		}
		
		
		   private List<Photo> photosOverview;
		   
		   @GetMapping("/photo")
		   public String displayPhoto(Map <String, Object> model, @RequestParam String id) {

			   String username = getUsername();
			   if(this.photosOverview == null) {
				   this.photosOverview = photoService.getAllPhotos(username);
			   }
			   List<Long> ids = photosOverview.stream().map(x -> x.getId()).collect(Collectors.toList());
			   //TODO: determine if id is first or last, populate values for id_index+1 and id_index-1 to correspond to next and previous photo IDs
			    Photo photo = photoService.getPhoto(id); //model.put("photo", photoService.getPhoto(id, username));
			    if(username.equals(photo.getUserId())) {
			    	model.put("photo", photo);
			    }
			   return "/photo";
		   }
		
	
}


