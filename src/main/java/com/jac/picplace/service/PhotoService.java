package com.jac.picplace.service;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jac.picplace.form.PhotoForm;
import com.jac.picplace.model.photo.Photo;
import com.jac.picplace.photoutils.PhotoSize;

public interface PhotoService {
	public boolean addPhoto(PhotoForm photoForm);
	public Page<Photo> getThumbnails(String username, Pageable pageable);
	public List<Photo> getAllPhotos(String username);
	public Photo getPhoto(String photoId);
	public int getNumberOfPages(Pageable page);
	public int getPhotosPerPage(Pageable page);
	public long getPhotoCountForUser(String userId);
	public List<Long> getPhotoIds(String userId);
	public void deletePhoto(String userId, long photoId);
	
	//public Page<Integer> getThumbnailIds(String userId, Pageable pageable);
	//public Photo getThumbnail(String userId, Integer photoId);
	//public void deletePhoto(String userId, Integer photoId);

	public String getImageBase64(String username, long photoId, PhotoSize photoSize);
}
