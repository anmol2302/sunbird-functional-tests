package org.sunbird.kp.test.content.v3;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.kp.test.common.APIUrl;
import org.sunbird.kp.test.common.BaseCitrusTestRunner;
import org.sunbird.kp.test.common.Constant;
import org.sunbird.kp.test.util.CollectionUtil;
import org.sunbird.kp.test.util.ContentUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class UpdateHierarchyTest extends BaseCitrusTestRunner {

    private static final String TEMPLATE_DIR = "templates/content/v3/hierarchy/update";
    private static final String MODE_EDIT = "?mode=edit";
    private static final String GET_HIERARCHY_RESPONSE_JSON = "hierarchy_response.json";
    private String identifier;
    private static List<String> resourceList = new ArrayList<>();

    @Test(dataProvider = "updateHierarchyWithValidRequest")
    @CitrusParameters("testName")
    @CitrusTest
    public void testUpdateHierarchyWithValidRequest(String testName) {
        resourceList = CollectionUtil.getLiveResources(this, 1, "application/pdf", null);
        identifier = (String) ContentUtil.createCollectionContent(this, null, "textbook", null).get("content_id");
        this.variable("rootId", identifier);
        this.variable("resourceId", resourceList.get(0));
        getAuthToken(this, Constant.CREATOR);
        performPatchTest(
                this,
                TEMPLATE_DIR,
                testName,
                APIUrl.UPDATE_CONTENT_HIERARCHY,
                null,
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                null,
                RESPONSE_JSON
        );
        performGetTest(
                this,
                TEMPLATE_DIR,
                testName,
                APIUrl.READ_CONTENT_HIERARCHY + identifier + MODE_EDIT,
                null,
                HttpStatus.OK,
                null,
                GET_HIERARCHY_RESPONSE_JSON
        );

    }


    @Test(dataProvider = "updateHierarchyWithResourceNotFoundRequest")
    @CitrusParameters("testName")
    @CitrusTest
    public void testUpdateHierarchyWithResourceNotFound(String testName) {
        resourceList = CollectionUtil.getLiveResources(this, 1, "application/pdf", null);
        identifier = (String) ContentUtil.createCollectionContent(this, null, "textbook", null).get("content_id");
        this.variable("rootId", identifier);
        this.variable("resourceId", resourceList.get(0));
        getAuthToken(this, Constant.CREATOR);
        performPatchTest(
                this,
                TEMPLATE_DIR,
                testName,
                APIUrl.UPDATE_CONTENT_HIERARCHY,
                null,
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                null,
                RESPONSE_JSON
        );
    }


    @Test(dataProvider = "updateHierarchyWithBadRequest")
    @CitrusParameters("testName")
    @CitrusTest
    public void testUpdateHierarchyWithBadRequest(String testName) {
        resourceList = CollectionUtil.getLiveResources(this, 1, "application/pdf", null);
        identifier = (String) ContentUtil.createCollectionContent(this, null, "textbook", null).get("content_id");
        this.variable("rootId", identifier);
        this.variable("resourceId", resourceList.get(0));
        getAuthToken(this, Constant.CREATOR);
        performPatchTest(
                this,
                TEMPLATE_DIR,
                testName,
                APIUrl.UPDATE_CONTENT_HIERARCHY,
                null,
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                null,
                RESPONSE_JSON
        );
    }

    @DataProvider
    public static Object[][] updateHierarchyWithBadRequest() {
        return new Object[][]{
                new Object[]{
                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITH_NO_ROOT_NODE_REQUEST
                },
                //TODO: Fix as Existing functionality throws 500 ISE
//                new Object[]{
//                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITH_INVALID_REQUEST
//                }
        };
    }

    @DataProvider
    public static Object[][] updateHierarchyWithValidRequest() {
        return new Object[][]{
                new Object[]{
                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITH_1UNIT_1RESOURCE_VALID_REQUEST
                },
                new Object[]{
                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITH_1UNIT_VALID_REQUEST
                },
                new Object[]{
                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITHOUT_UNIT_OR_RESOURCE
                },
                new Object[]{
                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITH_1RESOURCE_ONLY
                }
        };
    }

    @DataProvider
    public static Object[][] updateHierarchyWithResourceNotFoundRequest() {
        return new Object[][]{
                new Object[]{
                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITH_INVALID_UNIT_VALID_RESOURCE_REQUEST
                },
                new Object[]{
                        ContentV3Scenario.TEST_UPDATE_HIERARCHY_WITH_1UNIT_INVALID_RESOURCE_REQUEST
                }
        };
    }
}
