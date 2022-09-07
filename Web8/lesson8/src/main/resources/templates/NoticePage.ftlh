<#import "/spring.ftl" as spring>
<#import "commons.ftlh" as c>

<@c.page>
    <form method="post">
        <div class="text">
            <div class="name">
                <label for="text">Enter your Notice</label>
            </div>
            <label>
                <textarea name="content" style="width:15rem; height:10rem;"></textarea>
            </label>
        </div>
        <@c.error "noticeForm.content"/>
        <div class="button-field">
            <input type="submit" value="Add">
        </div>
    </form>
</@c.page>