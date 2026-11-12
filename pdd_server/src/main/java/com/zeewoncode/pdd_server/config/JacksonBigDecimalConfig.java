package com.zeewoncode.pdd_server.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * BigDecimal 全局序列化 / 反序列化配置（金额场景专用）
 * 作用：
 *  - 出参：BigDecimal -> 固定两位小数字符串，无科学计数法
 *  - 入参：数字/字符串/带千分位字符串 -> BigDecimal，自动清洗非法字符
 */
@Configuration
public class JacksonBigDecimalConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer bigDecimalCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("BigDecimalAmountModule");
            module.addSerializer(BigDecimal.class, new BigDecimalAmountSerializer());
            module.addDeserializer(BigDecimal.class, new BigDecimalAmountDeserializer());
            builder.modulesToInstall(module);
        };
    }

    /**
     * 序列化器：BigDecimal 输出为固定两位小数的纯字符串
     * 适用场景：金额类字段
     */
    static class BigDecimalAmountSerializer extends JsonSerializer<BigDecimal> {
        private static final int SCALE = 2;
        private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            // 四舍五入保留两位小数，toPlainString 彻底避免科学计数法
            String formatValue = value.setScale(SCALE, ROUNDING_MODE).toPlainString();
            gen.writeString(formatValue);
        }
    }

    static class BigDecimalAmountDeserializer extends JsonDeserializer<BigDecimal> {

        /** 只接受普通十进制，拒绝科学计数法、欧洲格式、正号等 */
        private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.currentToken() == JsonToken.VALUE_NULL) {
                return null;
            }

            String text = p.getText();
            if (text == null || text.isBlank()) {
                throw ctxt.weirdStringException(String.valueOf(text), BigDecimal.class, "数值不能为空");
            }

            // 允许千分位逗号，但清洗后必须匹配标准十进制格式
            String cleanText = text.trim().replace(",", "").replace(" ", "");
            if (!NUMBER_PATTERN.matcher(cleanText).matches()) {
                throw ctxt.weirdStringException(text, BigDecimal.class, "非法的数值格式");
            }

            return new BigDecimal(cleanText);
        }
    }
}
