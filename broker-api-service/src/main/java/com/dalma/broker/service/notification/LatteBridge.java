package com.dalma.broker.service.notification;

import com.dalma.broker.service.exception.latte.LatteAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LatteBridge extends BaseBridge {

	@Value("${dalma.latte.api.url}")
    private String latteUrl;

    @Value("${dalma.latte.api.connect.timeout}")
    private Integer connectTimeout;

    @Value("${dalma.latte.api.read.timeout}")
    private Integer readTimeout;

    public String postToLatte(String payload, LatteApiPath path, String id) {
        try {
        	return call(payload, path.getPath(id), latteUrl);
        } catch (IOException e) {
            throw new LatteAccessException(e.getMessage());
        }
    }
    
	@Override
	protected int getReadTimeout() {
		return this.readTimeout;
	}

	@Override
	protected int getConnectTimeout() {
		return this.connectTimeout;
	}
}
