package com.fhai.myregistry.cluster;

import com.fhai.myregistry.model.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Snapshot {
    MultiValueMap<String, InstanceMeta> REGISTRY;

    // 版本
    Map<String, Long> VERSIONS;
    Map<String, Long> TIMESTAMP;
    Long VERSION;
}
