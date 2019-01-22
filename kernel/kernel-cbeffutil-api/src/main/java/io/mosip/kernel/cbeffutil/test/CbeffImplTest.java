package io.mosip.kernel.cbeffutil.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.cbeffutil.common.CbeffISOReader;
import io.mosip.kernel.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.cbeffutil.service.impl.CbeffImpl;

public class CbeffImplTest {

	private List<BIR> createList;
	private List<BIR> updateList;
	private static final String tempPath = "./src/main/java/io/mosip/kernel/cbeffutil/test/resources";

	@Before
	public void setUp() throws Exception {
		byte[] fingerImg = CbeffISOReader.readISOImage(tempPath + "/images/" + "ISOImage.iso", "Finger");
		byte[] irisImg = CbeffISOReader.readISOImage(tempPath + "/images/" + "Sample_IRIS.iso", "Iris");

		BIR rFinger = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();

		BIR lFinger = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();

		BIR thumb = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Left Right Thumb")).withPurpose(PurposeType.ENROLL)
						.withLevel(ProcessedLevelType.RAW).withCreationDate(new Date()).build())
				.build();

		BIR face = new BIR.BIRBuilder().withBdb(new String("Test").getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(8))
						.withQuality(90).withType(Arrays.asList(SingleType.FACE)).withPurpose(PurposeType.ENROLL)
						.withLevel(ProcessedLevelType.RAW).build())
				.build();

		BIR leftIris = new BIR.BIRBuilder().withBdb(new String(irisImg).getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(9))
						.withQuality(80).withType(Arrays.asList(SingleType.IRIS)).withSubtype(Arrays.asList("Left"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).build())
				.build();

		BIR rightIris = new BIR.BIRBuilder().withBdb(new String(irisImg).getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(9))
						.withQuality(90).withType(Arrays.asList(SingleType.IRIS)).withSubtype(Arrays.asList("Right"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).build())
				.build();

		createList = new ArrayList<>();
		createList.add(rFinger);
		createList.add(lFinger);
		createList.add(thumb);
		createList.add(leftIris);
		createList.add(rightIris);
		createList.add(face);

		// Finger Minutiae is of Single Type - Finger and BDB Format Type - 2
		BIR fingerMinutiae = new BIR.BIRBuilder().withBdb(fingerImg)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(2))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList("Right IndexFinger MiddleFinger RingFinger LittleFinger"))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();

		updateList = new ArrayList<>();
		updateList.add(fingerMinutiae);

	}

	@Test
	public void testCreateXML() throws Exception {

		CbeffImpl cbeffImpl = new CbeffImpl();
		byte[] createXml = cbeffImpl.createXML(createList);
		createXMLFile(createXml, "createCbeff");
		assertEquals(new String(createXml), new String(readCreatedXML("createCbeff")));

	}

	private byte[] readCreatedXML(String name) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get(tempPath + "/schema/" + name + ".xml"));
		return fileContent;
	}

	private byte[] readXSD(String name) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get(tempPath + "/schema/" + name + ".xsd"));
		return fileContent;
	}

	private static void createXMLFile(byte[] updatedXmlBytes, String name) throws Exception {
		File tempFile = new File(tempPath + "/schema/" + name + ".xml");
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(updatedXmlBytes);
		fos.close();
	}

	@Test
	public void testUpdateXML() throws Exception {
		CbeffImpl cbeffImpl = new CbeffImpl();
		byte[] updateXml = cbeffImpl.updateXML(updateList, readCreatedXML("createCbeff"));
		createXMLFile(updateXml, "updateCbeff");
		assertEquals(new String(updateXml), new String(readCreatedXML("updateCbeff")));
	}

	@Test
	public void testValidateXML() throws IOException, Exception {
		CbeffImpl cbeffImpl = new CbeffImpl();
		assertTrue(cbeffImpl.validateXML(readCreatedXML("createCbeff"), readXSD("cbeff")));
	}
	
	@Test
	public void testGetTestElementDetails() {
		//TODO
	}

	@Test
	public void testGetBDBBasedOnType() throws IOException, Exception {
		CbeffImpl cbeffImpl = new CbeffImpl();
		Map<String,String> testMap = cbeffImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), "FMR", "Right");
		Set<String> testSet1 = new HashSet<>();
		testSet1.add("RIGHT FINGER");
		assertEquals(testMap.keySet(),testSet1);
		Map<String,String> testMap1 = cbeffImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), "FMR", null);
		Set<String> testSet2 = new HashSet<>();
		testSet2.add("FINGER Right IndexFinger MiddleFinger RingFinger LittleFinger 2");
		assertEquals(testMap1.keySet(),testSet2);
		Map<String,String> testMap2 = cbeffImpl.getBDBBasedOnType(readCreatedXML("updateCbeff"), null, "Right");
		Set<String> testSet3 = new HashSet<>();
		testSet3.add("Right IndexFinger MiddleFinger RingFinger LittleFinger FINGER 7");
		testSet3.add("Right IRIS 9");
		testSet3.add("Right IndexFinger MiddleFinger RingFinger LittleFinger FINGER 2");
		testSet3.add("Left Right Thumb FINGER 7");
		assertEquals(testMap2.keySet(),testSet3);
	}


}
