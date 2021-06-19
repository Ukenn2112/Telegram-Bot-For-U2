package jp.ka.command;

import jp.ka.config.U2;
import jp.ka.controller.Receiver;
import jp.ka.utils.CommonUtils;
import jp.ka.utils.HttpUtils;
import jp.ka.bean.RespGet;
import jp.ka.utils.Store;
import lombok.SneakyThrows;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.List;

public class CommandTools {

  private static final Receiver receiver = Store.context.getBean(Receiver.class);

  @SneakyThrows
  public static void userInfo(Long gid) {
    RespGet resp = HttpUtils.get(gid,"/index.php");
    Elements mediums = resp.getHtml().getElementsByClass("medium");
    if (mediums.size() == 0) {
      receiver.sendMsg(gid, "md", "*个人信息获取失败*", null);
      return;
    }
    Element medium = mediums.get(0);
    // =======
    Elements href = medium.getElementsByAttribute("href");
    Element user = href.get(0);
    U2.uid = user.attr("href").split("=")[1];
    U2.username = user.getElementsByAttribute("dir").get(0).text();
    U2.pageKey = href.get(1).attr("href").split("=")[1];
    // =======
    List<TextNode> text = medium.textNodes();
    U2.shareRate = text.get(7).toString().trim();
    U2.uploads = text.get(8).toString().trim();
    U2.downloads = text.get(9).toString().trim();
    U2.invite = text.get(12).splitText(1).toString().trim();
    if (text.size() == 16) {
      U2.client = text.get(13).toString().split(" ")[1].trim();
      U2.uploading = text.get(14).toString().trim();
      U2.downloading = text.get(15).toString().split("")[0].trim();
    } else {
      Elements a = medium.getElementsByTag("a");
      U2.client = a.get(9).text();
      Element a10 = a.get(10);
      String aLink = a10.attr("href").split("#")[1];
      if (aLink.equals("seedlist")) {
        U2.uploading = a10.text();
        U2.downloading = "0";
      } else if (aLink.equals("leechlist")) {
        U2.uploading = "0";
        U2.downloading = a10.text();
      }
      if (a.size() == 12) U2.downloading = a.get(11).text();
    }
    // ==================================
    Element uCoin = medium.getElementsByClass("ucoin-notation").get(0);
    U2.coinGold = uCoin.getElementsByClass("ucoin-gold").text();
    U2.coinSilver = uCoin.getElementsByClass("ucoin-silver").text();
    U2.coinCopper = uCoin.getElementsByClass("ucoin-copper").text();

    String tmpMsg = String.format("*\\[个人信息\\]*\n\nUID: `%s`\n用户名: `%s`\n分享率: `%s`\n上传量: `%s`\n下载量: `%s`\nUCoin: `%s%s%s`\n邀请: `%s`\n客户端: `%s`\n上传: `%s`\n下载: `%s`",
        U2.uid, CommonUtils.formatMD(U2.username), U2.shareRate, U2.uploads, U2.downloads, U2.coinGold.equals("") ? "" : "\uD83E\uDD47" + U2.coinGold, U2.coinSilver.equals("") ? "" : "\uD83E\uDD48" + U2.coinSilver, U2.coinCopper.equals("") ? "" : "\uD83E\uDD49" + U2.coinCopper, U2.invite, U2.client, U2.uploading, U2.downloading);
    receiver.sendMsg(gid, "md", tmpMsg, null);
  }

}
