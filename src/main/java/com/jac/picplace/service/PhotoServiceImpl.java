package com.jac.picplace.service;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jac.picplace.form.PhotoForm;
import com.jac.picplace.model.photo.Photo;
import com.jac.picplace.photoutils.PhotoSize;
import com.jac.picplace.repository.photo.PhotoFileRepository;
import com.jac.picplace.repository.photo.PhotoRepository;
import com.jac.picplace.rest.exception.BadRequestException;


@Service
public class PhotoServiceImpl implements PhotoService {


	@Autowired private PhotoRepository photoRepository;
	@Autowired private PhotoFileRepository photoFileRepository;

	   @Value("${file.directory}")
	   private String language;

	public PhotoServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Photo getPhoto(String photoId) {
		long id = Long.valueOf(photoId).longValue();
		System.out.println("photo ID = " + photoId);
		Optional<Photo> optional = photoRepository.findById(id);
		Photo photo = optional.orElse(new Photo());
		if(photo.isInitialised()) {
			photoFileRepository.attachPhotoData(photo, PhotoSize.MEDIUM);
		}else {
			System.out.println("photo is not initialised!");
		}
		return photo;
	}
	
	
	public boolean deletePhoto(String userId, int photoId) {
		
		long id = Long.valueOf(photoId).longValue();
		Optional<Photo> optional = photoRepository.findById(id);
		Photo photo = optional.orElse(new Photo());
		if(optional.isEmpty()) {
			return false;
		}
		photoRepository.delete(optional.get());
		photoFileRepository.delete(userId, photoId);
		
		return false;
	}
	
	//public Photo getf

	@Override
	@Transactional
	public boolean addPhoto(PhotoForm photoForm){
		
		String title = photoForm.getTitle();
		String username = photoForm.getUsername();
		byte[] bytes = getPhotoBytes(photoForm.getFile());
		if(bytes.length == 0) {
			return false;
		}
		
		Photo photo = new Photo();
		photo.setUserId(username);
		photo.setTitle(title);
		
		photoRepository.save(photo);
		long photoId = photo.getId();
		
		return photoFileRepository.save(bytes, username, photoId);
	}
	
	
	private byte[] getPhotoBytes(MultipartFile photoFile) {
		byte[] bytes = new byte[0];
		try {
			bytes = photoFile.getBytes();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	private long itemsCount; 
	
	@Override
	public int getNumberOfPages(Pageable page) {
		itemsCount = photoRepository.count();
		int itemsPerPage = page.getPageSize();
		int numberOfPages =  (int) (itemsCount / itemsPerPage);
		
		return numberOfPages == 0 ? 1 : numberOfPages;
	}
	
	@Override
	public long getPhotoCountForUser(String userId) {
		return photoRepository.countByUserId(userId);
	}
	
	@Override
	public int getPhotosPerPage(Pageable page) {
		int pageSize = page.getPageSize();
		return pageSize == 0 ? 20 : pageSize;
	}

	
	@Override
	public Page<Photo> getThumbnails(String username, Pageable pageable) {
		// TODO Auto-generated method stub
		Page<Photo> photos = photoRepository.findByUserId(username, pageable);
		
		photoFileRepository.attachThumbnailData(photos);
		return photos;
	}
	/*
	@Override List<Integer> getThumbnailIds(String userId, Pageable pageable){
		
		Page<Photo> photos = photoRepository.findByUserId(userId,  pageable);
		
		return photos.stream().map(u -> u.getId()).collect(Collectors.toList());
	}
*/
	@Override
	public List<Photo> getAllPhotos(String username) {
		return photoRepository.findByUserId(username);
	}
	
	@Override
	public void deletePhoto(String userId, long photoId) {
		
		validateUserAndResource(userId, photoId);
		photoRepository.deleteById(photoId);
		photoFileRepository.delete(userId, photoId);
	}
	

	public List<Long> getPhotoIds(String userId){
		return photoRepository.findByUserId(userId).stream().map(x -> x.getId()).collect(Collectors.toList());
	}
	

	public String getImageBase64(String userId, long photoId, PhotoSize photoSize) {
		validateUserAndResource(userId, photoId);
		return photoFileRepository.getImageData(userId, photoId, photoSize);
		
	}
	
	private void validateUserAndResource(String userId, Long photoId) {
		Optional<Photo> optional = photoRepository.findById(photoId);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException();
		}
		Photo photo = optional.get();
		if(!userId.equals(photo.getUserId())) {
			throw new BadRequestException();
		}
	}
	
	
	
}
