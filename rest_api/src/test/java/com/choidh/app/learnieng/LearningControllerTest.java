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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.choidh.app.common.AppConstant.API_DEFAULT_PATH;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("local")
@Import({RestDocsConfiguration.class})
class LearningControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
        this.mockMvc.perform(get(API_DEFAULT_PATH + "/learning/list")
                        .param("size", "10")
                        .param("sort", "createdAt")
                        .param("order", "desc")
                        .param("mainCategory", "all")
                        .param("subCategory", "")
                        .accept(MediaTypes.HAL_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("get-learningList"
                        , links(
                                 linkWithRel("self").description("메인")
                        )
                        , requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("응답 타입")
                        )
                        , requestParameters(
                                parameterWithName("size").description("페이징 요청 크기"),
                                parameterWithName("sort").description("페이징 기준 값"),
                                parameterWithName("order").description("페이징 정렬 방식"),
                                parameterWithName("mainCategory").description("메인 카테고리. (all, java)"),
                                parameterWithName("subCategory").description("서브 카테고리. (WEB, ALGORITHM)")
                        )
                        , relaxedResponseFields(
                                fieldWithPath("learningVOList").description("강의 목록")
                        ).andWithPrefix("learningVOList[].", getLearningResponseFields())
                ));
    }
}