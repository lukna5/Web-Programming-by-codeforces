<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <#if user?? && user.admin>
                    <th>Admin</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="user_id"></td>
                    <td class="user_login"></td>
                    <#if user?? && user.admin>
                        <td> <a href="" class="user_admin">Loading...</a></td>
                    </#if>
                </tr>
            </template>
            <tr class="noData">
                <td colspan="3">
                    No data
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <script>
        $(function () {
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");
            const data = {
                action: "findAll"
            };
            const success = function(response) {
                response["users"].forEach(function(user) {
                    $noData.hide();
                    const $tr = $($(".users template").prop("content")).clone();
                    $tr.find(".user_id").text(user["id"]);
                    $tr.find(".user_login").text(user["login"]);
                    if (user["admin"]) {
                        $tr.find(".user_admin").text("disable");
                        $tr.find(".user_admin").attr('value', "disable");
                    } else {
                        $tr.find(".user_admin").text("enable");
                        $tr.find(".user_admin").attr('value', "enable");
                    }
                    $tr.find("tr").attr("data-userId", user["id"]);
                    $tr.find("td a").attr("data-userId", user["id"]);
                    $(".users tbody").append($tr);
                });
                $.notify(userr)
                $(".users .user_admin").click(function () {
                    const $form = $(this);
                    const status = ($form.attr('value') === 'disable');
                    // $.notify("value = " + $form.attr('value'))
                    // if (status) $.notify("You are admin")
                    const data = {
                        action: "changeAdmin",
                        userId: $(this).attr("data-userId"),
                        status
                    };
                    const success = function () {
                        if (status) {
                            $form.text("enable");
                            $form.attr('value', "enable");
                        } else {
                            $form.text("disable");
                            $form.attr('value', "disable");
                        }
                    };
                    ajax(data, success);
                    return false;
                });
                $(".users tr").click(function() {
                    const data = {
                        action: "findUser",
                        userId: $(this).attr("data-userId")
                    };
                    const success =  function (response) {
                        notify(response["user"].creationTime, 'success');
                    };
                    ajax(data,success);
                });
            };
            ajax(data, success);
        })
    </script>
    <script>
        $(function () {
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");
            const data = {
                action: "findAll"
            }
            const success = function(response) {
                response["users"].forEach(function (user) {
                    $noData.hide();
                    const $tr = $($(".users template").prop("content")).clone();
                    $tr.find(".user-id").text(user["id"]);
                    $tr.find(".user_login").text(user["login"]);
                    $tr.find("tr").attr("data-userId", user["id"]);
                    $(".users tbody").append($tr);
                });
                $(".users tr").click(function () {
                    const success = function (response) {
                        notify(response["user"].creationTime);
                    }
                    const data = {
                        action: "findUser",
                        userId: $(this).attr("data-userId")
                    }
                    ajax(data, success())
                });
            }
            $noData.hide()
            ajax(data, success())
        })
    </script>
</@c.page>
