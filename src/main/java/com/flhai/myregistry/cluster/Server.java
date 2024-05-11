package com.flhai.myregistry.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "url")
public class Server {
    private String url;
    private boolean status;
    private boolean leader;
    private long version;
}
