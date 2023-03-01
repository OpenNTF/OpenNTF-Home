package api.atompub;

import api.atompub.model.AppCategories;
import api.atompub.model.AtomCategory;
import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import model.util.PostUtil;

@Path(AtomPubResource.BASE_PATH + "/{blogId}/categories")
@RolesAllowed(UserInfoBean.ROLE_BLOGADMIN)
public class CategoriesResource {
    @GET
    @Produces("application/atomserv+xml")
    public AppCategories list() {
    	AppCategories categories = new AppCategories();
    	categories.setFixed(false);

        PostUtil.getCategories()
        	.map(AtomCategory::new)
        	.forEach(categories.getCategories()::add);

        return categories;
    }
}
