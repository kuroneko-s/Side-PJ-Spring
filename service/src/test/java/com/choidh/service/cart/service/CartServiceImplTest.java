package com.choidh.service.cart.service;

import com.choidh.service.AbstractServiceTestConfig;
import com.choidh.service.account.entity.Account;
import com.choidh.service.professional.entity.ProfessionalAccount;
import com.choidh.service.account.vo.RegAccountVO;
import com.choidh.service.cart.entity.Cart;
import com.choidh.service.learning.entity.Learning;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Rollback(value = false)
class CartServiceImplTest extends AbstractServiceTestConfig {
    @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    @Rollback
    @DisplayName("카트 생성")
    public void regCart() throws Exception {
        RegAccountVO regAccountVO = RegAccountVO.builder()
                .nickname("sample")
                .email("sample")
                .password(passwordEncoder.encode("1234567890"))
                .passwordcheck(passwordEncoder.encode("1234567890"))
                .build();
        Account accountVO = modelMapper.map(regAccountVO, Account.class);

        Account account = accountRepository.save(accountVO);

        theLine();

        Cart cart = cartService.regCart(account.getId());
        assertNotNull(cart);
    }

    @Test
    @DisplayName("유저의 카트에 강의 추가.")
    public void addCart() throws Exception {
        ProfessionalAccount professionalAccount = createProfessionalAccount(createAccount());

        Account account = createAccount("test2", "test2@test.com");
        for (int i = 0; i < 4; i++) {
            Learning learning = createLearning(professionalAccount);

            cartService.addCart(account.getId(), learning.getId());
        }

        theLine();



    }

}