package org.sunbird.kp.test.search.v3;

import com.consol.citrus.annotations.CitrusTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang.StringUtils;
import org.sunbird.kp.test.common.BaseCitrusTestRunner;
import org.sunbird.kp.test.common.Constant;
import org.sunbird.kp.test.util.CompositeSearchUtil;
import org.sunbird.kp.test.util.ContentUtil;
import org.sunbird.kp.test.util.SearchPayload;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Functional Test Cases for search-service (Composite Search Api)
 *
 * @author Kumar Gauraw
 */
public class CompositeSearchTest extends BaseCitrusTestRunner {

	private static final String TEMPLATE_DIR = "templates/search/v3/search";

	@Test
	@CitrusTest
	public void testSearchForTextBookHavingRelatedBoards() throws JsonProcessingException {
		String testName = "testSearchForTextBookHavingConsumeAs";
		this.getTestCase().setName(testName);
		getAuthToken(this, Constant.CREATOR);
		//TODO: Get Board Using Framework API.
		String board = "ICSE";
		String anotherBoard = "testboard"+generateRandomDigits(5);
		String textbookId = (String) ContentUtil.createCollectionContent(this, null, "textbook", null).get("content_id");
		System.out.println("Textbook Id : " + textbookId);
		String updateReqPayload = "{\"request\":{\"content\":{\"board\":\"boardVal\"}}}".replace("boardVal", board);
		String updatedTbId = (String) ContentUtil.systemUpdate(this, testContext, textbookId, updateReqPayload, testName, null).get("content_id");
		Assert.assertTrue(StringUtils.isNotBlank(textbookId) && StringUtils.isNotBlank(updatedTbId));
		String consumableTextbookId = (String) ContentUtil.createCollectionContent(this, null, "textbook", null).get("content_id");
		String payload = "{\"request\":{\"content\":{\"relatedBoards\":boardVal}}}".replace("boardVal", objectMapper.writeValueAsString(new ArrayList<String>(){{
			add(board);
			add(anotherBoard);
		}}));
		String updatedId = (String) ContentUtil.systemUpdate(this, testContext, consumableTextbookId, payload, testName, null).get("content_id");
		Assert.assertTrue(StringUtils.isNotBlank(textbookId) && StringUtils.isNotBlank(updatedId));
		ContentUtil.publishContent(this, null, "public", consumableTextbookId, null);
		delay(this, 60000);
		//search content and validate
		String searchPayload = SearchPayload.SEARCH_CONTENT_WITH_BOARD.replace("boardVal", board);
		System.out.println("searchPayload : " + searchPayload);
		Map<String, Object> searchResult = CompositeSearchUtil.searchContent(this, searchPayload, testName, null);
		System.out.println("count : "+searchResult.get("count"));
		List<Map<String, Object>> content = (List<Map<String, Object>>) searchResult.get("content");
		boolean found = false;
		for (Map<String, Object> record : content) {
			if (record.containsKey("relatedBoards")) {
				List<String> relatedBoards = (List<String>)record.get("relatedBoards");
				for(String relBoard:relatedBoards){
					if (StringUtils.equals(relBoard, anotherBoard) && StringUtils.equals(consumableTextbookId, (String) record.get("identifier"))) {
						found = true;
					}
				}
			}
		}
		Assert.assertTrue(found);
	}
}