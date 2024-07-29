package com.choidh.app.learnieng;

import com.choidh.app.RestDocsConfiguration;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.choidh.app.common.AppConstant.API_DEFAULT_PATH;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.relaxedLinks;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("local")
@Import({RestDocsConfiguration.class})
class LearningControllerTest {
    @Autowired private MockMvc mockMvc;

    private FieldDescriptor[] getPageInfo() {
        return new FieldDescriptor[]{
                fieldWithPath("page.size").description("한 페이지에 보여주는 정보 수").type(JsonFieldType.NUMBER),
                fieldWithPath("page.totalElements").description("전체 데이터 갯수").type(JsonFieldType.NUMBER),
                fieldWithPath("page.totalPages").description("전체 페이지 갯수").type(JsonFieldType.NUMBER),
                fieldWithPath("page.number").description("현재 페이지. 기본 0").type(JsonFieldType.NUMBER)
        };
    }

    private FieldDescriptor[] getLearningResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("강의 고유값").type(JsonFieldType.NUMBER),
                fieldWithPath("title").description("강의 제목").type(JsonFieldType.STRING),
                fieldWithPath("subscription").description("강의 설명").type(JsonFieldType.STRING),
                fieldWithPath("simpleSubscription").description("강의 짧은 설명").type(JsonFieldType.STRING),
                fieldWithPath("price").description("강의 가격").type(JsonFieldType.NUMBER),
                fieldWithPath("mainCategory").description("강의 주 카테고리").type(JsonFieldType.STRING),
                fieldWithPath("subCategory").description("강의 보조 카테고리").type(JsonFieldType.STRING),
                fieldWithPath("openingDate").description("강의 개설일자").type(JsonFieldType.STRING),
                fieldWithPath("opening").description("강의 공개여부").type(JsonFieldType.BOOLEAN),
                fieldWithPath("rating").description("강의 평점").type(JsonFieldType.NUMBER),
                fieldWithPath("professionalAccountName").description("강의 개설자 이름").type(JsonFieldType.STRING),
                fieldWithPath("professionalAccountDescription").description("강의 개설자 설명").type(JsonFieldType.STRING),
                fieldWithPath("professionalAccountHistory").description("강의 개설자 경력").type(JsonFieldType.STRING),
                fieldWithPath("tags").description("강의 카테고리").type(JsonFieldType.ARRAY)
        };
    }

    @Test
    @DisplayName("Get 강의 목록 조회")
    public void getLearningList() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get(API_DEFAULT_PATH + "/learning/list")
                        .param("page", "3")
                        .param("size", "10")
                        .param("sort", "createdAt")
                        .param("order", "desc")
                        .param("mainCategory", "all")
                        .param("subCategory", "")
                        .accept(MediaTypes.HAL_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-learningList"
                        , relaxedLinks(
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("last").description("마지막 페이지"),
                                linkWithRel("first").description("첫 페이지"),
                                linkWithRel("next").description("다음 페이지").optional(),
                                linkWithRel("prev").description("이전 페이지").optional()
                        )
                        , requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("응답 타입")
                        )
                        , requestParameters(
                                parameterWithName("page").description("요청 페이지. 시작 0"),
                                parameterWithName("size").description("페이징 요청 크기"),
                                parameterWithName("sort").description("페이징 기준 값"),
                                parameterWithName("order").description("페이징 정렬 방식"),
                                parameterWithName("mainCategory").description("메인 카테고리. (all, java)"),
                                parameterWithName("subCategory").description("서브 카테고리. (WEB, ALGORITHM)")
                        )
                        , relaxedResponseFields(
                                fieldWithPath("_embedded.learningVOList").description("강의 목록")
                        ).andWithPrefix("_embedded.learningVOList[].", getLearningResponseFields())
                                .and(
                                        getPageInfo()
                                )
                ));
    }

    @Test
    @DisplayName("Get 강의 단건 조회")
    public void getLearningDetail() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get(API_DEFAULT_PATH + "/learning/{learningId}", 1454)
                        .accept(MediaTypes.HAL_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-learningDetail"
                        , relaxedLinks(
                                linkWithRel("self").description("상세조회")
                        )
                        , requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("응답 타입")
                        )
                        , pathParameters(
                                parameterWithName("learningId").description("조회 강의 고유값")
                        )
                        , relaxedResponseFields(
                                getLearningResponseFields()
                        )
                ));
    }
}