# OpenNTF Home

This application is the in-progress new OpenNTF home application, developed as an NSF using the [XPages Jakarta EE Support project](https://github.com/OpenNTF/org.openntf.xsp.jakartaee/). Other than that project, it has no external code dependencies.

In particular, the technologies and techniques used are:

- Data access is done by way of Jakarta NoSQL, with entities configured in the [`model` Java package and sub-packages](nsf-home/odp/Code/Java/model)
- Utility functions and coordinating aspects are written in CDI beans, kept in the [`beans` Java package](nsf-home/odp/Code/Java/bean)
- The UI is written using Jakarta MVC and JSP, with the controller classes kept in the [`webapp.controller` Java package](nsf-home/odp/Code/Java/webapp/controller), the JSP elements kept in [`tags` and `views` within `WEB-INF`](nsf-home/odp/WebContent/WEB-INF), and the stylesheets kept in the [`Resources/StyleSheets` folder](nsf-home/odp/Resources/StyleSheets)
- Blog entries are also accessible via REST APIs written using Jakarta REST classes kept in the [`api` package and sub-packages](nsf-home/odp/Code/Java/api)

## Deployment

Currently, deploying this essentially requires being OpenNTF, as the data is stored in several databases that are not included in this repository. That may change in the future.

## License

This project is licensed under the Apache 2.0 License. See [NOTICE.md](NOTICE.md) for copyright information and details of third-party code.