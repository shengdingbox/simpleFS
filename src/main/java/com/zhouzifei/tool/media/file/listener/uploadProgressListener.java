package com.zhouzifei.tool.media.file.listener;

import com.zhouzifei.tool.entity.VirtualFile;
import com.zhouzifei.tool.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @author 周子斐
 * @date 2021/2/3
 * @Description
 */
@Slf4j
public class uploadProgressListener implements ProgressListener {
    @Override
    public void start(String msg) {
        log.info("开始处理任务,文件名为{}",msg);
    }

    @Override
    public void process(int finished, int sum) {
        float percent = new BigDecimal(finished).divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP)
                .floatValue();
//        BigDecimal bigDecimal = new BigDecimal(downloadBytes.toString());
//        final String speed = StringUtils.convertToDownloadSpeed(new BigDecimal(downloadBytes.toString()).subtract(bigDecimal), 2) + "/s";
        log.info("已下载" + finished + "个\t一共" + sum + "个\t已完成" + percent + "%"+percent + "% (" + finished + "/" + sum + ")");
//                ""+"下载速度为"+speed);

    }

    @Override
    public void end(VirtualFile virtualFile) {
        log.info("任务处理完成,完成的地址为{}",virtualFile);
    }
}
