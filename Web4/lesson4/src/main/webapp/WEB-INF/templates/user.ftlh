<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#-- @ftlvariable name="used_id" type="java.lang.Long" -->
<#import "./common.ftlh" as c>
<link href="../css/userStyle.css" rel="stylesheet"/>

<@c.page>
    77
    ${used_id}
    <#if handle ??>
        l
        <#assign u = c.findBy(users, "handle", handle)!>
    <#elseif id ??>
        f
        <#assign  u = c.findBy(users, "id", used_id)!/>
    </#if>
    ${u.id}
    <#if u?has_content>
    <div class="page">
        <div class="left">
            <@c.userlink u/>
            <div class="info">
            <ul>
                <li>id: ${u.id} </li>
                <li>handle: ${u.handle} </li>
                <li>name: ${u.name}</li>
            </ul>
            </div>
        </div>
        <div class="right">
            <img alt="Icon" title="Users Icon" src="/img/userPhoto.png">
        </div>
    </div>
        <div class="arrows">
            <span class="left-arrow">
            <#if c.hasPrev(users, "handle", u.handle)==true>
                <a href="user?handle=${c.prev(users, "handle", u.handle)}"> &#10229; </a>
            <#else>
                <span>&#10229;</span>
            </#if>
            </span>
            <span class="right-arrow">
            <#if c.hasNext(users, "handle", u.handle)==true>
                <a href="user?handle=${c.next(users, "handle", u.handle)}"> &#10230; </a>
            <#else>
                <span>&#10230;</span>
            </#if>
            </span>
        </div>

    <#else>
        No such user
    </#if>


</@c.page>
