package org.mbalves.sp.crypto.wallet.adapter.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.adapter.pricing.dto.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.ResponseEntity.ok;

@ExtendWith(MockitoExtension.class)
class CoinCapAdapterTest {

    @InjectMocks
    private CoinCapAdapter coinCapAdapter;

    @Mock
    private RestTemplate restTemplate;
    private String tokenId;

    @BeforeEach
    void setUp() {
        tokenId = "bitcoin";
        // Inject the mocked RestTemplate into the adapter
        ReflectionTestUtils.setField(coinCapAdapter, "restTemplate", restTemplate);
    }

    @Test
    void getToken_WhenApiReturnsValidResponseWithMatchingSymbol_ShouldReturnToken() {
        // Arrange
        CoinCapListResponse response = mock(CoinCapListResponse.class);
        CoinCapData data = mock(CoinCapData.class);
        when(data.getSymbol()).thenReturn("BTC");
        when(data.getId()).thenReturn("bitcoin");
        when(data.getPriceUsd()).thenReturn("50000.0");
        when(response.getData()).thenReturn(java.util.List.of(data));
        when(data.getSymbol()).thenReturn("BTC");
    
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapListResponse.class), eq("BTC")))
            .thenReturn(ok(response));
    
        ReflectionTestUtils.setField(coinCapAdapter, "apiUrlSymbol", "url");
    
        // Act
        var token = coinCapAdapter.getToken("BTC");
    
        // Assert
        assertNotNull(token);
        assertEquals("BTC", token.getSymbol());
        assertEquals("bitcoin", token.getId());
        assertEquals(50000.0, token.getPrice().doubleValue());
    }
    
    @Test
    void getToken_WhenApiReturnsNull_ShouldReturnNull() {
        // Arrange
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapListResponse.class), eq("BTC")))
            .thenReturn(ok(null));
    
        ReflectionTestUtils.setField(coinCapAdapter, "apiUrlSymbol", "url");
    
        // Act
        var token = coinCapAdapter.getToken("BTC");
    
        // Assert
        assertNull(token);
    }
    
    @Test
    void getToken_WhenApiReturnsEmptyData_ShouldReturnNull() {
        // Arrange
        CoinCapListResponse response = mock(CoinCapListResponse.class);
        when(response.getData()).thenReturn(java.util.Collections.emptyList());
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapListResponse.class), eq("BTC")))
            .thenReturn(ok(response));
    
        ReflectionTestUtils.setField(coinCapAdapter, "apiUrlSymbol", "url");
    
        // Act
        var token = coinCapAdapter.getToken("BTC");
    
        // Assert
        assertNull(token);
    }
    
    @Test
    void getToken_WhenApiReturnsDataWithNonMatchingSymbol_ShouldReturnNull() {
        // Arrange
        CoinCapListResponse response = mock(CoinCapListResponse.class);
        CoinCapData data = mock(CoinCapData.class);
        when(data.getSymbol()).thenReturn("ETH");
        when(response.getData()).thenReturn(java.util.List.of(data));
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapListResponse.class), eq("BTC")))
            .thenReturn(ok(response));
    
        ReflectionTestUtils.setField(coinCapAdapter, "apiUrlSymbol", "url");
    
        // Act
        var token = coinCapAdapter.getToken("BTC");
    
        // Assert
        assertNull(token);
    }
    
    @Test
    void getToken_WhenApiThrowsException_ShouldReturnNull() {
        // Arrange
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapListResponse.class), eq("BTC")))
            .thenThrow(new RestClientException("API error"));
    
        ReflectionTestUtils.setField(coinCapAdapter, "apiUrlSymbol", "url");
    
        // Act
        var token = coinCapAdapter.getToken("BTC");
    
        // Assert
        assertNull(token);
    }
    
    @Test
    void getTokenPrice_WhenApiReturnsValidResponse_ShouldReturnPrice() {
        // Arrange
        String priceUsd = "50000.0";
        
        CoinCapData data = new CoinCapData();
        data.setPriceUsd(priceUsd);
        
        CoinCapPriceResponse response = new CoinCapPriceResponse();
        response.setData(data);
        ResponseEntity<CoinCapPriceResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(response);
        
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin")))
            .thenReturn(responseEntity);

        // Act
        Double result = coinCapAdapter.getTokenPrice(tokenId);

        // Assert
        assertNotNull(result);
        assertEquals(50000.0, result);
        verify(restTemplate).exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin"));
    }

    @Test
    void getTokenPrice_WhenApiReturnsNull_ShouldReturnNull() {
        // Arrange
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin")))
            .thenReturn(ok(null));

        // Act
        Double result = coinCapAdapter.getTokenPrice(tokenId);

        // Assert
        assertNull(result);
        verify(restTemplate).exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin"));
    }

    @Test
    void getTokenPrice_WhenApiReturnsResponseWithNullData_ShouldReturnNull() {
        // Arrange
        CoinCapPriceResponse response = new CoinCapPriceResponse();
        response.setData(null);
        ResponseEntity<CoinCapPriceResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(response);

        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin")))
            .thenReturn(responseEntity);

        // Act
        Double result = coinCapAdapter.getTokenPrice(tokenId);

        // Assert
        assertNull(result);
        verify(restTemplate).exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin"));
    }

    @Test
    void getTokenPrice_WhenApiThrowsException_ShouldReturnNull() {
        // Arrange
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin")))
            .thenThrow(new RestClientException("API error"));

        // Act
        Double result = coinCapAdapter.getTokenPrice(tokenId);

        // Assert
        assertNull(result);
        verify(restTemplate).exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin"));
    }

    @Test
    void getTokenPrice_WhenApiReturnsDataWithInvalidPrice_ShouldReturnNull() {
        // Arrange
        CoinCapData data = new CoinCapData();
        data.setPriceUsd("not-a-number");
        CoinCapPriceResponse response = new CoinCapPriceResponse();
        response.setData(data);
        ResponseEntity<CoinCapPriceResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(response);

        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin")))
            .thenReturn(responseEntity);

        // Act
        Double result = coinCapAdapter.getTokenPrice(tokenId);

        // Assert
        assertNull(result);
        verify(restTemplate).exchange(any(), any(), any(), eq(CoinCapPriceResponse.class), eq("bitcoin"));
    }
    
    @Test
    void getTokenPrice_WithDate_WhenApiReturnsValidResponse_ShouldReturnPrice() {
        // Arrange
        String priceUsd = "42000.0";
        CoinCapDataHistory data = new CoinCapDataHistory();
        data.setPriceUsd(priceUsd);
        CoinCapPriceHistoryResponse response = new CoinCapPriceHistoryResponse();
        response.setData(java.util.List.of(data));
        ResponseEntity<CoinCapPriceHistoryResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(response);
    
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceHistoryResponse.class), any(), any(), any()))
            .thenReturn(responseEntity);
    
        LocalDate date = LocalDate.of(2024, 1, 1);
    
        // Act
        Double result = coinCapAdapter.getTokenPrice("bitcoin", date);
    
        // Assert
        assertNotNull(result);
        assertEquals(42000.0, result);
    }
    
    @Test
    void getTokenPrice_WithDate_WhenApiReturnsNull_ShouldReturnNull() {
        // Arrange
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceHistoryResponse.class), any(), any(), any()))
            .thenReturn(ok(null));
    
        LocalDate date = LocalDate.of(2024, 1, 1);
    
        // Act
        Double result = coinCapAdapter.getTokenPrice("bitcoin", date);
    
        // Assert
        assertNull(result);
    }
    
    @Test
    void getTokenPrice_WithDate_WhenApiReturnsResponseWithNullData_ShouldReturnNull() {
        // Arrange
        CoinCapPriceHistoryResponse response = new CoinCapPriceHistoryResponse();
        response.setData(null);
        ResponseEntity<CoinCapPriceHistoryResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(response);
    
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceHistoryResponse.class), any(), any(), any()))
            .thenReturn(responseEntity);
    
        LocalDate date = LocalDate.of(2024, 1, 1);
    
        // Act
        Double result = coinCapAdapter.getTokenPrice("bitcoin", date);
    
        // Assert
        assertNull(result);
    }
    
    @Test
    void getTokenPrice_WithDate_WhenApiReturnsEmptyDataList_ShouldReturnNull() {
        // Arrange
        CoinCapPriceHistoryResponse response = new CoinCapPriceHistoryResponse();
        response.setData(java.util.Collections.emptyList());
        ResponseEntity<CoinCapPriceHistoryResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(response);
    
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceHistoryResponse.class), any(), any(), any()))
            .thenReturn(responseEntity);
    
        LocalDate date = LocalDate.of(2024, 1, 1);
    
        // Act
        Double result = coinCapAdapter.getTokenPrice("bitcoin", date);
    
        // Assert
        assertNull(result);
    }
    
    @Test
    void getTokenPrice_WithDate_WhenApiReturnsDataWithInvalidPrice_ShouldReturnNull() {
        // Arrange
        CoinCapDataHistory data = new CoinCapDataHistory();
        data.setPriceUsd("not-a-number");
        CoinCapPriceHistoryResponse response = new CoinCapPriceHistoryResponse();
        response.setData(java.util.List.of(data));
        ResponseEntity<CoinCapPriceHistoryResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(response);
    
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceHistoryResponse.class), any(), any(), any()))
            .thenReturn(responseEntity);
    
        LocalDate date = LocalDate.of(2024, 1, 1);
    
        // Act
        Double result = coinCapAdapter.getTokenPrice("bitcoin", date);
    
        // Assert
        assertNull(result);
    }
    
    @Test
    void getTokenPrice_WithDate_WhenApiThrowsException_ShouldReturnNull() {
        // Arrange
        when(restTemplate.exchange(any(), any(), any(), eq(CoinCapPriceHistoryResponse.class), any(), any(), any()))
            .thenThrow(new RestClientException("API error"));
    
        LocalDate date = LocalDate.of(2024, 1, 1);
    
        // Act
        Double result = coinCapAdapter.getTokenPrice("bitcoin", date);
    
        // Assert
        assertNull(result);
    }
}
