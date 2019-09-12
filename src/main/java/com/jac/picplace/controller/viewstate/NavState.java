package com.jac.picplace.controller.viewstate;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class NavState {

	private boolean isNavVisible = true;
	private boolean isNextNavEnabled = false;
	private boolean isPreviousNavEnabled = false;
	
	
	public NavState() {
		// TODO Auto-generated constructor stub
	}

	
	public void assignState(Page<?> items, Pageable currentPage) {
		assignNavState(items);
		if(isNavVisible) {
			assignNavPreviousState(items);
			assignNavNextState(items, currentPage);
		}	
		navPreviousLink = assignNavLink(items, currentPage, -1, isPreviousNavEnabled);
		navNextLink = assignNavLink(items, currentPage, +1, isNextNavEnabled);
	}
	
	private String navNextLink;
	private String navPreviousLink;
	
	public String getNavPreviousLink() {
		return navPreviousLink;
	}
	public String getNavNextLink() {
		return this.navNextLink;
	}
	
	public String assignNavLink(Page<?> items, Pageable currentPage, int newPageOffset, boolean isNavVisible) {
		if(isNavVisible) {
			int newPageIndex = currentPage.getPageNumber() + newPageOffset;
			String sortedParam = currentPage.getSort().isUnsorted() ? "" : "&sort=" + currentPage.getSort().toString();
			return "/?page=" + newPageIndex + "&size=" + currentPage.getPageSize() + sortedParam;
		}	
		return "";
	}
	
	public void assignNavState(Page<?> items) {
		this.isNavVisible = items.getTotalPages() > 1;
	}

	public void assignNavPreviousState(Page<?> items) {
		isPreviousNavEnabled = !items.isFirst();
	}
	
	public void assignNavNextState(Page<?> items, Pageable currentPage) {
		int currentPageNumber = currentPage.getPageNumber();
		int totalPages = items.getTotalPages();
	
		isNextNavEnabled = currentPageNumber < totalPages -1;
		
	}
	

	
	
	public boolean getIsNavVisible() {
		return this.isNavVisible;
	}
	
	public boolean getIsNextNavEnabled() {
		return this.isNextNavEnabled;
	}
	
	public boolean getIsPreviousNavEnabled() {
		return this.isPreviousNavEnabled;
	}
	
	public boolean hasNoResults() {
		return false;
	}
	
	
}
