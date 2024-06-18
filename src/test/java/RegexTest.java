import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.wrapper.WrapperJmeterDsl.testElement;

import java.io.IOException;

import org.apache.jmeter.extractor.XPathExtractor;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

public class RegexTest {

    @Test
    public void test() throws IOException {
        TestPlanStats stats = testPlan(
                httpCache()
                        .disable(),
                httpCookies()
                        .disable(),
                threadGroup("UC01_T01_page_open", 1, 1,
                        httpSampler("https://habr.com/ru/articles/349860/")
                                .children(testElement("article", new XPathExtractor())
                                                .prop("XPathExtractor.refname", "article")
                                                .prop("XPathExtractor.matchNumber", "1")
                                                .prop("XPathExtractor.xpathQuery", "//div[@id = 'post-content-body']")
                                                .prop("XPathExtractor.tolerant", true)
                                                .prop("XPathExtractor.fragment", true),
                                        regexExtractor("headers", "<h2>(.*?)<\\/h2>")
                                                .matchNumber(-1)
                                ),
                        jsr223Sampler("handle_format_write", "def textBlocks = vars.get(\"article\").split(\"<h2>(.*?)</h2>\") // разделяем код страницы (блока) на разделы\n"
                                + "\n"
                                + "def file = new File(\"file.txt\")\n"
                                + "\n"
                                + "if (!file.exists()) {\n"
                                + "    file.createNewFile()\n"
                                + "}\n"
                                + "\n"
                                + "def fileWriter = new FileWriter(file, false)\n"
                                + "def printWriter = new PrintWriter(fileWriter)\n"
                                + "\n"
                                + "int headerNumber = vars.get(\"headers_matchNr\") as int // получаем количество разделов\n"
                                + "\n"
                                + "for (int i = 1; i <= headerNumber; i++) {\n"
                                + "    def header = vars.get(\"headers_${i}_g1\") // получаем заголовок раздела\n"
                                + "    \n"
                                + "    def formattedHeader = header.replaceAll(\"<[^>]+>\", \"\") // удаляем все html-теги из заголовка с помощью regex\n"
                                + "\n"
                                + "    def cleanedTextBlock = textBlocks[i].replaceAll(\"<[^>]+>\", \"\") // удаляем все html-теги из соответствующего раздела с тем же regex\n"
                                + "\n"
                                + "    int length = cleanedTextBlock.length()\n"
                                + "\n"
                                + "    printWriter.println(\"${i} : ${formattedHeader} : ${length}\") // построчная запись в файл в требуемом формате\n"
                                + "}\n"
                                + "\n"
                                + "printWriter.close()\n")
                ),
                resultsTreeVisualizer()
        ).run();
        assertThat(stats.overall().errorsCount()).isEqualTo(0);
    }
}
