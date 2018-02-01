在Web开发中,有些页面需要判断用户是否已经登录. 在Django中,判断方法如下:

```

    {% if user.is_authenticated%}

      {{user.username}}

    {%else%}

      没有登录

    {%endif%}

```

问题在于,我们没有在view中传递user对象. 那么这个变量从哪里来呢?

在Django的文档中有描述:https://docs.djangoproject.com/en/1.8/topics/auth/default/ 

```

When rendering a template RequestContext, the currently logged-in user, either a User instance or an AnonymousUser instance, is stored in the template variable {{ user }}:

```

看来是Django框架内部帮我们处理的. 通过跟踪Django的源码, 我们可以找到

```

#django.template.context.py

    @contextmanager

    def bind_template(self, template):

        if self.template is not None:

            raise RuntimeError("Context is already bound to a template")



        self.template = template

        # Set context processors according to the template engine's settings.

        processors = (template.engine.template_context_processors +

                      self._processors)

        updates = {}

        for processor in processors:

            updates.update(processor(self.request))

        self.dicts[self._processors_index] = updates

```

在`template.engine`中有5个上下文处理器(context processor),它们分别用来处理`crfs` `debug` `request` `auth` `message` , 并将'message' 'perms' 'user' 'scrf_token' 'request'这些变量保存作为template的变量,供用户使用. 

除了这些变量, context中还有'True' 'False' 'None' 这个三变量 .  通过调试Django Template的源码, 对"Python试图用字典装下一切" 就有更深的体会了.



讲到'user'变量, 就不得不提'perms'变量了,因为它们是一对CP.  user用于判定你是不是圈内人, perms用于判定你在这个圈内的级别.







像user/perms这些内置变量, 有点像Servlet的9大内置对象. 
