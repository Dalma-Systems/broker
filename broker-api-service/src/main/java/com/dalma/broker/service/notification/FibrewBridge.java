package com.dalma.broker.service.notification;

import com.dalma.broker.service.exception.fibrew.FibrewAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FibrewBridge extends BaseBridge {

	@Value("${dalma.fibrew.api.url}")
    private String fibrewUrl;

    @Value("${dalma.fibrew.api.connect.timeout}")
    private Integer connectTimeout;

    @Value("${dalma.fibrew.api.read.timeout}")
    private Integer readTimeout;

    public String postToFibrew(String payload, FibrewApiPath path) {
        try {
            return call(payload, path.getPath(), fibrewUrl);
        } catch (IOException e) {
            throw new FibrewAccessException(e.getMessage());
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
