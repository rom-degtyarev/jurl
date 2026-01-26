package ru.jurl.filter;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.jurl.http.RequestMessage;
import ru.jurl.http.ResponseMessage;
import ru.jurl.support.Messages;

import static ru.jurl.support.Formats.duration;

@Data
public class LoggerFilter implements Filter<RequestMessage, ResponseMessage> {
    private static final Logger logger = LoggerFactory.getLogger(LoggerFilter.class);

    private final Messages.BodyPrintType printType;

    @Override
    public ResponseMessage intercept(Invocation<RequestMessage, ResponseMessage> invocation) {
        long start = System.currentTimeMillis();
        ResponseMessage response = invocation.invoke();
        long time = System.currentTimeMillis() - start;

        StringBuilder text = new StringBuilder();
        text.append("\n>>>>>>>>>> HTTP call duration %s >>>>>>>>>>\n".formatted(duration(time)));
        text.append(Messages.toString(invocation.getInput(), printType));
        text.append("\n");
        text.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        text.append(Messages.toString(response, printType));
        logger.debug(text.toString());
        return response;
    }
}
