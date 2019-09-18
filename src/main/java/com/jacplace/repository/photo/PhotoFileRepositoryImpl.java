package com.jacplace.repository.photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.jac.picplace.model.photo.Photo;
import com.jac.picplace.photoutils.FilepathResolver;
import com.jac.picplace.photoutils.PhotoSize;
import com.jac.picplace.photoutils.PhotoUtils;

@Component
public class PhotoFileRepositoryImpl implements PhotoFileRepository {

	@Autowired private FilepathResolver filepathResolver;
	@Autowired private PhotoUtils photoUtils;
	
	public PhotoFileRepositoryImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean save(byte[] bytes, String userId, long photoId) {
		return  savePhoto(bytes,userId, photoId, PhotoSize.LARGE) && 
				savePhoto(bytes,userId, photoId, PhotoSize.MEDIUM) && 
				savePhoto(bytes,userId, photoId, PhotoSize.THUMBNAIL);
	}
	
	@Override
	public void delete(String userId, long photoId) {
		
		deletePhoto(userId, photoId, PhotoSize.LARGE);
		deletePhoto(userId, photoId, PhotoSize.MEDIUM);
		deletePhoto(userId, photoId, PhotoSize.THUMBNAIL);
	}
	
	
	private void deletePhoto(String userId, long photoId, PhotoSize photoSize) {
		String path = filepathResolver.getPhotoPath(userId,  photoId,  photoSize);
		File file = new File(path);
		file.delete();
		
	}
	@Override
	public void attachPhotoData(Photo photo, PhotoSize photoSize) {
		String path = filepathResolver.getPhotoPath(photo.getUserId(), photo.getId(), photoSize);
		String photoStr = getPhotoData(path);
		
		switch(photoSize) {
		case THUMBNAIL:
			photo.setThumbnailData(photoStr);
			break;
		case MEDIUM:
			photo.setMediumData(photoStr);
			break;
		case LARGE:
			photo.setLargeData(photoStr);
		}
	}
	
	@Override
	public String getImageData(String userId, long photoId, PhotoSize photoSize) {
		
		String path = filepathResolver.getPhotoPath(userId,  photoId,  photoSize);
		return getPhotoData(path);
	}
	
	
		
	
	@Override
	public void attachThumbnailData(Page<Photo> photos) {
		for(Photo photo : photos) {
			String path = filepathResolver.getPhotoPath(photo.getUserId(), photo.getId(), PhotoSize.THUMBNAIL);
			photo.setThumbnailData(getPhotoData(path));
		}
	}
	
	
	private boolean savePhoto(byte[] bytes, String username, long photoId, PhotoSize photoSize) {
		
		byte[] bytesToWrite = photoUtils.createByteArray(bytes, photoSize);
		String path = filepathResolver.getPhotoPath(username, photoId, photoSize);
		
		return writeToFile(bytesToWrite, path);
	}
	
	private boolean savePhoto(byte[] bytes, String username, long photoId) {
		return writeToFile(bytes, filepathResolver.getPhotoPath(username, photoId, PhotoSize.LARGE));
	}
	
	
	private boolean writeToFile(byte[] bytes, String filename) {
		
		boolean success = false;
		
		 try (OutputStream outputStream = new FileOutputStream(filename);
				 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){  
	
			 byteArrayOutputStream.write(bytes);
			 byteArrayOutputStream.writeTo(outputStream);
			 success = true;
		 } catch (IOException e) {  
		   e.printStackTrace();  
		 }
		return success;
	}
	
	
	private String getPhotoData(String path) {
		
		File f = new File(path);
		byte[] content = new byte[(int) f.length()];

		try (InputStream in = new FileInputStream(f)){
			System.out.println("Reading file: "+  path);
		    for (int off = 0, read;
		        (read = in.read(content, off, content.length - off)) > 0;
		        off += read);	    
		    
		    return DatatypeConverter.printBase64Binary(content);
		    
		} catch (IOException e) {
		    // Some error occured
		}
		
		return "";		
	}
}
