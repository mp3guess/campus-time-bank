package com.campus.timebank.service;

import com.campus.timebank.dto.WalletDto;
import com.campus.timebank.entity.Wallet;
import com.campus.timebank.mapper.WalletMapper;
import com.campus.timebank.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    
    @Transactional(readOnly = true)
    public WalletDto getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));
        
        return walletMapper.toDto(wallet);
    }
}
