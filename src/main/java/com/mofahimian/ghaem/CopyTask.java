package com.mofahimian.ghaem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CopyTask implements Runnable {

	private String address;

	private String dstAddress;

	private String srcAddress;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private ApplicationContext appCtx;

	@Autowired
	private StringEncryptor jasyptStringEncryptor;

	@Override
	public void run() {
		Path path = Paths.get(address);
		if (Files.isRegularFile(path)) {
//			System.out.println(srcAddress);
//			System.out.println(
//					 String pathString = path.getParent().toString().replace(srcAddress, "").replaceAll("/", "").replaceAll("\\\\", "");
//					 System.out.println(pathString);
//					 String encryptedPathString = jasyptStringEncryptor.encrypt(pathString);
			try {
				Files.copy(path, Paths.get(dstAddress + "/"
						+ jasyptStringEncryptor.encrypt(path.getParent().toString().replace(srcAddress, "")
								.replaceAll("/", "").replaceAll("\\\\", ""))
						+ "." + path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (Files.isDirectory(path)) {
			try {
				Files.list(path).forEach(
						f -> taskExecutor.submit(appCtx.getBean(CopyTask.class, f.toString(), dstAddress, srcAddress)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public CopyTask(String address, String dstAddress, String srcAddress) {
		this.address = address;
		this.dstAddress = dstAddress;
		this.srcAddress = srcAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDstAddress() {
		return dstAddress;
	}

	public void setDstAddress(String dstAddress) {
		this.dstAddress = dstAddress;
	}

	public String getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(String srcAddress) {
		this.srcAddress = srcAddress;
	}

}
