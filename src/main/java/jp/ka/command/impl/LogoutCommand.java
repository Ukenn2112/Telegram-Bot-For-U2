package jp.ka.command.impl;

import jp.ka.command.Command;
import jp.ka.variable.U2Info;
import jp.ka.controller.Receiver;
import jp.ka.utils.HttpUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class LogoutCommand implements Command {

  @Autowired
  private Receiver receiver;

  @SneakyThrows
  @Override
  public void execute(Message msg) {
    Long gid = msg.getChatId();

    HttpUtils.get(gid, "/logout.php?key=" + U2Info.pageKey);
    HttpUtils.session.clear();
    receiver.sendMsg(gid, "md", "*登出成功！*", null);
  }

  @Override
  public CMD cmd() {
    return CMD.LOGOUT;
  }

  @Override
  public Boolean needLogin() {
    return true;
  }

  @Override
  public String description() {
    return "登出";
  }

  @Override
  public Message prompt(Long gid) {
    return null;
  }

}
