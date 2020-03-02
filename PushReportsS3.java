package com.leafBot.testcases;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class PushReportsS3{

	// WebDriver driver;
	String baseurl, nodeURL;
	public static ExtentHtmlReporter html;
	public static ExtentReports extent;
	public static ExtentTest test, suiteTest;

	@Test
	public void setup() throws MalformedURLException {

		html = new ExtentHtmlReporter("./reports/result.html");
		html.setAppendExisting(false);
		extent = new ExtentReports();
		extent.attachReporter(html);
		baseurl = "https://demo-stable.ofbiz.apache.org/partymgr/control/main";

		// Hub URL
		nodeURL = "http://192.168.1.9:4444/wd/hub";
		DesiredCapabilities dc = new DesiredCapabilities();
		dc.setBrowserName("chrome");
		dc.setPlatform(Platform.WINDOWS);
		RemoteWebDriver driver = new RemoteWebDriver(new URL(nodeURL), dc);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseurl);

		// sample test script
		driver.findElement(By.name("USERNAME")).sendKeys("admin");
		driver.findElement(By.name("PASSWORD")).sendKeys("ofbiz");
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		
		suiteTest = extent.createTest("TC0001_Smoke Test", "Sanity Test");
		test = suiteTest.createNode("Demo");
		test.info("Sanity Test");
		extent.flush();

		// Push reports to S3 bucket
		AWSCredentials credentials = new BasicAWSCredentials("YourAccessKeyID", "YourAccessSecretKey");

		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);

		// push object to S3 bucket
		s3client.putObject(new PutObjectRequest("YourBucketName", "report.html", new File("./reports/result.html"))
				.withCannedAcl(CannedAccessControlList.PublicRead));
	}
}