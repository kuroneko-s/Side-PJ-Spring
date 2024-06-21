package com.choidh.service.cart.service;

import com.choidh.service.account.entity.Account;
import com.choidh.service.account.service.AccountService;
import com.choidh.service.attachment.entity.AttachmentFile;
import com.choidh.service.attachment.entity.AttachmentFileType;
import com.choidh.service.attachment.service.AttachmentService;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.cart.repository.CartRepository;
import com.choidh.service.cart.vo.BuyVO;
import com.choidh.service.joinTables.entity.LearningCartJoinTable;
import com.choidh.service.joinTables.service.LearningCartService;
import com.choidh.service.learning.entity.Learning;
import com.choidh.service.learning.service.LearningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.choidh.service.common.CodeConstant.DUPLICATED;
import static com.choidh.service.common.CodeConstant.SUCCESS;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    @Autowired private CartRepository cartRepository;
    @Autowired private LearningCartService learningCartService;
    @Autowired private AccountService accountService;
    @Autowired private LearningService learningService;
    @Autowired private AttachmentService attachmentService;

    /**
     * 카트 생성
     */
    @Override
    @Transactional
    public Cart regCart(Long accountId) {
        Account account = accountService.getAccountById(accountId);

        return cartRepository.save(Cart.builder()
                .account(account)
                .learningCartJoinTables(new HashSet<>())
                .build());
    }

    /**
     * 카트 조회 By Account Id
     */
    @Override
    public Cart getCart(Long accountId) {
        return cartRepository.findByAccountIdWithLearningCartJoinTables(accountId);
    }

    /**
     * 유저의 카트에 강의 추가.
     */
    @Override
    @Transactional
    public String addCart(Long accountId, Long learningId) {
        Account account = accountService.getAccountByIdWithLearningInCart(accountId);
        Learning learning = learningService.getLearningById(learningId);

        for (LearningCartJoinTable learningCartJoinTable : account.getCart().getLearningCartJoinTables()) {
            if (learningCartJoinTable.getLearning().getId().equals(learningId)) {
                return DUPLICATED;
            }
        }

        LearningCartJoinTable learningCartJoinTable = new LearningCartJoinTable();
        learningCartJoinTable.setLearning(learning);
        learningCartJoinTable = learningCartService.saveLearningCart(learningCartJoinTable);

        account.getCart().addLearningCartJoinTable(learningCartJoinTable);

        return SUCCESS;
    }

    /**
     * 카트에서 강의 삭제
     */
    @Override
    @Transactional
    public void deleteCart(Cart cart, List<Learning> learningList) {
        for (Learning learning : learningList) {
            int result = learningCartService.removeLearning(cart.getId(), learning.getId());

            if (result != 1)
                throw new IllegalArgumentException("삭제 실패. cart id - " + cart.getId() + ", learning id - " + learning.getId());
        }
    }

    /**
     * 구매 화면 View
     */
    @Override
    @Transactional
    public BuyVO buyCartView(Long accountId, @Nullable Long learningId) {
        // 강의 상세 화면의 구매하기 버튼으로 접근. 일반적으로는 장바구니에서 접근함.
        if (learningId != null) {
            // 카트에 강의 등록
            this.addCart(accountId, learningId);
        }

        Account account = accountService.getAccountById(accountId);
        Set<LearningCartJoinTable> learningCartJoinTableList = learningCartService.getCartListWithLearningByCartId(account.getCart().getId());
        List<Learning> learningList = learningCartJoinTableList.stream().map(LearningCartJoinTable::getLearning).collect(Collectors.toList());

        // 강의 이미지 목록 조회
        Map<Long, List<String>> learningImageMap = new HashMap<>();
        for (Learning learning : learningList) {
            List<AttachmentFile> attachmentFiles = attachmentService.getAttachmentFiles(learning.getAttachmentGroup().getId(), AttachmentFileType.BANNER);
            if (attachmentFiles.size() != 1) {
                throw new IllegalArgumentException();
            }

            List<String> valueList = learningImageMap.getOrDefault(learning.getId(), new ArrayList<>());

            AttachmentFile attachmentFile = attachmentFiles.get(0);
            valueList.add(attachmentFile.getFullPath(""));
            valueList.add(attachmentFile.getOriginalFileName());
            learningImageMap.put(learning.getId(), valueList);
        }

        return BuyVO.builder()
                .learningList(learningList)
                .learningImageMap(learningImageMap)
                .totalPrice(learningList.stream().mapToInt(Learning::getPrice).sum())
                .build();
    }
}
