package com.lt.blog.config;

import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.dto.Result;
import com.lt.blog.util.ResultGenerator;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @program: FruitSales
 * @description: 全局异常统一处理类
 **/

@ControllerAdvice
public class ApplicationControllerExceptionHandler {

    /**
     * 方法参数效验
     * @param e
     * @return com.zhulin.ascentweb.dto.Result
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Result<String> BindException(BindException e) {
        return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }


    /**
     * 方法参数效验
     * @param e
     * @return com.zhulin.ascentweb.dto.Result
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<String> handlerError(HttpServletRequest req, Exception e) {
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR,"出现异常错误,请及时查看后台日志！");
    }
}
