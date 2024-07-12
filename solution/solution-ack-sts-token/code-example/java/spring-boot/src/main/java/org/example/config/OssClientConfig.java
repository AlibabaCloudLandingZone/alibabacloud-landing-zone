package org.example.config;

import com.aliyun.credentials.models.CredentialModel;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.comm.SignVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssClientConfig {

    @Autowired
    com.aliyun.credentials.Client credentialClient;

    @Bean(name = "ossClient")
    OSS getOssClient() {
        // Bucket所在地域对应的Endpoint，以华东1（杭州）为例
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        // Endpoint对应的Region信息，例如cn-hangzhou。
        String region = "cn-hangzhou";
        // 建议使用更安全的V4签名算法，则初始化时需要加入endpoint对应的region信息，同时声明SignVersion.V4
        // OSS Java SDK 3.17.4及以上版本支持V4签名。
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setSignatureVersion(SignVersion.V4);

        return OSSClientBuilder.create()
            .endpoint(endpoint)
            .credentialsProvider(new CredentialsProvider() {
                @Override
                public void setCredentials(Credentials credentials) {
                }

                @Override
                public Credentials getCredentials() {
                    // 保证线程安全，从 CredentialModel 中获取 ak/sk/security token
                    CredentialModel credentialModel = credentialClient.getCredential();
                    String ak = credentialModel.getAccessKeyId();
                    String sk = credentialModel.getAccessKeySecret();
                    String token = credentialModel.getSecurityToken();
                    return new DefaultCredentials(ak, sk, token);
                }
            })
            .clientConfiguration(configuration)
            .region(region)
            .build();
    }
}
