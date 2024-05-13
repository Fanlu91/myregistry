# 注册中心

zk可以做注册中心，但是zk重点关注的是一致性，实现cp。 注册中心里保存的都是系统的在线状态，实际上可以存在一定的不一致，重点是高可用，因此nacos之类的注册中心是ap的。

# 学习nacos的模型

nacos主要实现了naming 注册 和 config 配置，这两者底座很像，基础特性很像。

这里我们重点关注naming

一般来说服务是长期存在的，服务的实例是多变的，注册中心的实现也遵循这样的逻辑

- 服务作为持久化的信息注册在中心中，之后除了版本和元数据的变化外一般不太变动
  
  - Nacos中指Naming Maintain Service
  
  - 在其他地方服务本身的信息变化抽象出来是元数据中心，是业务逻辑的一部分，和实例并不直接关联。

- 实例的操作是在不断变化、通知的
  
  - Nacos中指Naming Service





# 知识点

## fastjson不支持反序列化接口

```java

public class Snapshot {
    MultiValueMap<String, InstanceMeta> REGISTRY;
    // 版本
    Map<String, Long> VERSIONS;
    Map<String, Long> TIMESTAMP;
    Long VERSION;
}

```



`return JSON.parseObject(respJson, Snapshot);` 报`com.alibaba.fastjson.JSONException: unsupport type interface org.springframework.util.MultiValueMap`



由于 `MultiValueMap` 是一个接口，`fastjson` 并不知道应该实例化哪个具体的实现类。在这种情况下，你需要提供一个具体的类来指导反序列化的过程。

一种解决方法是确保在反序列化时指定一个具体的实现类型，而不是接口。`LinkedMultiValueMap` 是 `MultiValueMap` 的一个常用实现，我们可以使用它来解决这个问题。



## @RestControllerAdvice

`@RestControllerAdvice` 是 Spring Framework 中的一个注解，它结合了 `@ControllerAdvice` 和 `@ResponseBody` 的功能，用于在全局范围内处理控制器抛出的异常，并返回 JSON 格式的响应。

以下是 `@RestControllerAdvice` 的主要功能和用法：

1. **全局异常处理**：`@RestControllerAdvice` 允许你在一个地方集中处理应用程序中所有控制器抛出的异常。这使得异常处理代码更加集中和可维护。

2. **返回 JSON 响应**：`@RestControllerAdvice` 自动将异常处理方法的返回值序列化为 JSON 格式。这对于 RESTful API 非常有用，因为客户端通常期望接收到 JSON 响应。



以下是一个简单的 `@RestControllerAdvice` 示例，展示了如何处理全局异常并返回 JSON 响应：

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>("Illegal argument: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

在上面的示例中，`GlobalExceptionHandler` 类使用 `@RestControllerAdvice` 注解。该类包含两个异常处理方法：

- `handleIllegalArgumentException` 方法处理 `IllegalArgumentException` 异常，并返回 HTTP 400 (Bad Request) 状态码和异常消息。
- `handleGeneralException` 方法处理所有其他类型的异常，并返回 HTTP 500 (Internal Server Error) 状态码和异常消息。

用法说明

- **注解**：`@RestControllerAdvice` 注解在类上，表示这个类是一个全局异常处理器。
- **方法**：使用 `@ExceptionHandler` 注解在方法上，指定该方法处理哪种类型的异常。
- **返回类型**：异常处理方法的返回类型通常是 `ResponseEntity`，包含响应的主体和状态码。

优点

- **集中管理**：所有异常处理逻辑集中在一个地方，易于管理和维护。
- **一致性**：确保所有异常响应的格式和结构一致，提升了 API 的一致性。
- **简化控制器代码**：控制器类无需再处理异常逻辑，使其更专注于业务逻辑。

通过使用 `@RestControllerAdvice`，你可以有效地管理和处理 Spring Boot 应用程序中的异常，提高代码的可读性和可维护性。
