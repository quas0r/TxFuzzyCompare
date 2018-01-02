package tutuka;

import org.springframework.web.multipart.MultipartFile;

public class FileLoader {

	private MultipartFile fileOne;
	private MultipartFile fileTwo;

	public MultipartFile getFileOne() {
		return fileOne;
	}

	public void setFileOne(MultipartFile fileOne) {
		this.fileOne = fileOne;
	}

	public MultipartFile getFileTwo() {
		return fileTwo;
	}

	public void setFileTwo(MultipartFile fileTwo) {
		this.fileTwo = fileTwo;
	}

}