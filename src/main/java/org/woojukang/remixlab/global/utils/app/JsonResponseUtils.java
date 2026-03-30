package org.woojukang.remixlab.global.utils.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@RequiredArgsConstructor
public class JsonResponseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeJsonResponse(HttpStatus statusCode,
                                         HttpServletResponse response,
                                         Object object)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode.value());

        String json = objectMapper.writeValueAsString(object);
        response.getWriter().write(json);
    }
}
