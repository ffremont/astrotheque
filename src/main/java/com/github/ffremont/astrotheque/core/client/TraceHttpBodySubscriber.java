package com.github.ffremont.astrotheque.core.client;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow;

@Slf4j
public class TraceHttpBodySubscriber implements Flow.Subscriber<ByteBuffer> {

    private static final long UNBOUNDED = Long.MAX_VALUE;


    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(UNBOUNDED);
    }

    @Override
    public void onNext(ByteBuffer item) {
        log.info(new String(item.array(), StandardCharsets.UTF_8));
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void onComplete() {
    }

}