package com.mofahimian.ghaem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class CopierApplication {

	public static void main(String[] args) {
		ApplicationContext appCtx = SpringApplication.run(CopierApplication.class, args);
		String act = appCtx.getEnvironment().getProperty("act");
		
		if (act.equals("enc")) {
			String src = appCtx.getEnvironment().getProperty("src");
			String dst = appCtx.getEnvironment().getProperty("dst");
			try (Stream<Path> walk = Files.walk(Paths.get(src))) {
				 walk.filter(Files::isRegularFile).filter(file -> !file.toFile().isHidden()).forEach(f -> appCtx.getBean(ThreadPoolTaskExecutor.class).submit(appCtx.getBean(CopyTask.class , f, dst, src)));
			} catch (IOException e) {
				e.printStackTrace();
			}
//			appCtx.getBean(ThreadPoolTaskExecutor.class).submit(appCtx.getBean(CopyTask.class , src, dst, src));
		} else if (act.equals("dec")) {
			String cip = appCtx.getEnvironment().getProperty("cip");
			System.out.println("-----------------------");
			System.out.println(appCtx.getBean("jasyptStringEncryptor",StringEncryptor.class).decrypt(cip));
			System.out.println("-----------------------");
		}
		
//		System.out.println(appCtx.getBean("jasyptStringEncryptor",StringEncryptor.class).decrypt("0ACE515ED96995B373F11035B252781D"));
//		System.out.println(appCtx.getBean("jasyptStringEncryptor",StringEncryptor.class).decrypt("D396948B571BD014E01D4FBB554C58C7"));
//		System.out.println(appCtx.getBean("jasyptStringEncryptor",StringEncryptor.class).decrypt("FD29560F359693E54F7499770D08A29E"));
		
		
	}

}
