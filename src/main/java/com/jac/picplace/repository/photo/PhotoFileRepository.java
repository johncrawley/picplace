package com.jac.picplace.repository.photo;

import org.springframework.data.domain.Page;

import com.jac.picplace.model.photo.Photo;
import com.jac.picplace.photoutils.PhotoSize;

public interface PhotoFileRepository {

	public boolean save(byte[] bytes, String userId, long photoId);
	public void attachThumbnailData(Page<Photo> photos);
	public String getImageData(String userId, long photoId, PhotoSize photoSize);
	public void attachPhotoData(Photo photo, PhotoSize photoSize);
	public void delete(String userId, long photoId);

}
