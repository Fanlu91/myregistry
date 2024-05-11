package com.flhai.myregistry.cluster;

import com.flhai.myregistry.model.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Snapshot {
    LinkedMultiValueMap<String, InstanceMeta> REGISTRY;
    // 版本
    Map<String, Long> VERSIONS;
    Map<String, Long> TIMESTAMP;
    Long VERSION;
}
