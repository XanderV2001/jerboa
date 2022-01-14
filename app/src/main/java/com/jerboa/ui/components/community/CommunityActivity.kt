package com.jerboa.ui.components.community

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.openLink
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.person.personClickWrapper
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.postClickWrapper

@Composable
fun CommunityActivity(
    navController: NavController,
    communityViewModel: CommunityViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
    accountViewModel: AccountViewModel,
) {

    Log.d("jerboa", "got to community activity")

    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val accounts by accountViewModel.allAccounts.observeAsState()
    val account = getCurrentAccount(accounts = accounts)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Column {
                    communityViewModel.res?.community_view?.community?.name?.also {
                        CommunityHeader(
                            communityName = it,
                            selectedSortType = communityViewModel.sortType.value,
                            onClickSortType = { sortType ->
                                communityViewModel.fetchPosts(
                                    account = account,
                                    clear = true,
                                    changeSortType = sortType,
                                    ctx = ctx,
                                )
                            },
                            navController = navController,
                        )
                    }
                    if (communityViewModel.loading.value) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            content = {
                PostListings(
                    contentAboveListings = {
                        communityViewModel.res?.community_view?.also {
                            CommunityTopSection(communityView = it)
                        }
                    },
                    posts = communityViewModel.posts,
                    onUpvoteClick = { postView ->
                        communityViewModel.likePost(
                            voteType = VoteType.Upvote,
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onDownvoteClick = { postView ->
                        communityViewModel.likePost(
                            voteType = VoteType.Downvote,
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onPostClick = { postView ->
                        postClickWrapper(
                            postViewModel = postViewModel,
                            postId = postView.post.id,
                            account = account,
                            navController = navController,
                            ctx = ctx,
                        )
                    },
                    onPostLinkClick = { url ->
                        openLink(url, ctx)
                    },
                    onSaveClick = { postView ->
                        communityViewModel.savePost(
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onCommunityClick = { communityId ->
                        communityClickWrapper(
                            communityViewModel,
                            communityId,
                            account,
                            navController,
                            ctx = ctx,
                        )
                    },
                    onSwipeRefresh = {
                        communityViewModel.fetchPosts(
                            account = account,
                            clear = true,
                            ctx = ctx,
                        )
                    },
                    loading = communityViewModel.loading.value &&
                        communityViewModel.page.value == 1 &&
                        communityViewModel.posts.isNotEmpty(),
                    isScrolledToEnd = {
                        communityViewModel.fetchPosts(
                            account = account,
                            nextPage = true,
                            ctx = ctx,
                        )
                    },
                    onPersonClick = { personId ->
                        personClickWrapper(
                            personProfileViewModel = personProfileViewModel,
                            personId = personId,
                            account = account,
                            navController = navController,
                            ctx = ctx,
                        )
                    },
                )
            },
        )
    }
}