package model.util;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import com.ibm.commons.util.StringUtil;

import model.blog.BlogEntry;

import jakarta.enterprise.inject.spi.CDI;

public enum PostUtil {
	;
	
	public static int parseStartParam(final String startParam) {
        int start;
        if(StringUtil.isNotEmpty(startParam)) {
            try {
                start = Integer.parseInt(startParam);
            } catch(NumberFormatException e) {
                start = -1;
            }
        } else {
            start = -1;
        }
        return start;
    }
	
	public static long getPostCount() {
		return CDI.current().select(BlogEntry.Repository.class)
			.get()
			.findAll()
			.count();
	}
	
	public static BlogEntry createPost() {
		BlogEntry post = new BlogEntry();
		post.setDate(OffsetDateTime.now());
		return post;
	}
	
	public static Stream<String> getCategories() {
		return CDI.current().select(BlogEntry.Repository.class)
			.get()
			.listCategories()
			.map(BlogEntry::getCategories)
			.filter(cats -> !cats.isEmpty())
			.map(cats -> cats.get(0));
	}
}
